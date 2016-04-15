package com.shivang.performance;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.log.Slf4jSpanLogger;
import org.springframework.cloud.sleuth.log.SpanLogger;

public class CustomSlf4jSpanLogger implements SpanLogger {

    private final Logger log;
    private final Pattern nameSkipPattern;
    @Autowired
    private TraceKeys traceKeys;

    public CustomSlf4jSpanLogger(String nameSkipPattern) {
        this.nameSkipPattern = Pattern.compile(nameSkipPattern);
        this.log = org.slf4j.LoggerFactory
                .getLogger(Slf4jSpanLogger.class);
    }

    CustomSlf4jSpanLogger(String nameSkipPattern, Logger log) {
        this.nameSkipPattern = Pattern.compile(nameSkipPattern);
        this.log = log;
    }

    @Override
    public void logStartedSpan(Span parent, Span span) {
        MDC.put(Span.SPAN_ID_NAME, Span.idToHex(span.getSpanId()));
        MDC.put(Span.SPAN_EXPORT_NAME, String.valueOf(span.isExportable()));
        MDC.put(Span.TRACE_ID_NAME, Span.idToHex(span.getTraceId()));
        log("Starting span: {}", span);
        if (parent != null) {
            log("With parent: {}", parent);
            for (String header : traceKeys.getHttp().getHeaders()) {
                MDC.put(header, parent.tags().get(header));
            }
        } else {
            for (String header : traceKeys.getHttp().getHeaders()) {
                MDC.put(header, span.tags().get(header));
            }
        }
    }

    @Override
    public void logContinuedSpan(Span span) {
        MDC.put(Span.SPAN_ID_NAME, Span.idToHex(span.getSpanId()));
        MDC.put(Span.TRACE_ID_NAME, Span.idToHex(span.getTraceId()));
        MDC.put(Span.SPAN_EXPORT_NAME, String.valueOf(span.isExportable()));
        for (String header : traceKeys.getHttp().getHeaders()) {
            MDC.put(header, span.tags().get(header));
        }
        log("Continued span: {}", span);
    }

    @Override
    public void logStoppedSpan(Span parent, Span span) {
        log("Stopped span: {}", span);
        if (parent != null) {
            log("With parent: {}", parent);
            MDC.put(Span.SPAN_ID_NAME, Span.idToHex(parent.getSpanId()));
            MDC.put(Span.SPAN_EXPORT_NAME, String.valueOf(parent.isExportable()));
            for (String header : traceKeys.getHttp().getHeaders()) {
                MDC.put(header, parent.tags().get(header));
            }
        } else {
            MDC.remove(Span.SPAN_ID_NAME);
            MDC.remove(Span.SPAN_EXPORT_NAME);
            MDC.remove(Span.TRACE_ID_NAME);
            for (String header : traceKeys.getHttp().getHeaders()) {
                MDC.remove(header);
            }
        }
    }

    private void log(String text, Span span) {
        if (this.nameSkipPattern.matcher(span.getName()).matches()) {
            return;
        }
        this.log.trace(text, span);
    }

}
