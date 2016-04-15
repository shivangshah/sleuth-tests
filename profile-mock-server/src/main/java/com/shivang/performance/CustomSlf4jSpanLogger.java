package com.shivang.performance;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.log.Slf4jSpanLogger;
import org.springframework.cloud.sleuth.log.SpanLogger;

public class CustomSlf4jSpanLogger implements SpanLogger {

    private final Logger log;
    private final Pattern nameSkipPattern;

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
            if (parent.tags().get(TraceHeaders.X_FORWARDED_FOR) != null) {
                MDC.put(TraceHeaders.X_FORWARDED_FOR, parent.tags().get(TraceHeaders.X_FORWARDED_FOR));
            }
            if (parent.tags().get(TraceHeaders.X_GID_CLIENT_SESSION) != null) {
                MDC.put(TraceHeaders.X_GID_CLIENT_SESSION, parent.tags().get(TraceHeaders.X_GID_CLIENT_SESSION));
            }
        } else {
            if (span.tags().get(TraceHeaders.X_FORWARDED_FOR) != null) {
                MDC.put(TraceHeaders.X_FORWARDED_FOR, span.tags().get(TraceHeaders.X_FORWARDED_FOR));
            }
            if (span.tags().get(TraceHeaders.X_GID_CLIENT_SESSION) != null) {
                MDC.put(TraceHeaders.X_GID_CLIENT_SESSION, span.tags().get(TraceHeaders.X_GID_CLIENT_SESSION));
            }
        }
    }

    @Override
    public void logContinuedSpan(Span span) {
        MDC.put(Span.SPAN_ID_NAME, Span.idToHex(span.getSpanId()));
        MDC.put(Span.TRACE_ID_NAME, Span.idToHex(span.getTraceId()));
        MDC.put(Span.SPAN_EXPORT_NAME, String.valueOf(span.isExportable()));
        if (span.tags().get(TraceHeaders.X_FORWARDED_FOR) != null) {
            MDC.put(TraceHeaders.X_FORWARDED_FOR, span.tags().get(TraceHeaders.X_FORWARDED_FOR));
        }
        if (span.tags().get(TraceHeaders.X_GID_CLIENT_SESSION) != null) {
            MDC.put(TraceHeaders.X_GID_CLIENT_SESSION, span.tags().get(TraceHeaders.X_GID_CLIENT_SESSION));
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
            if (parent.tags().get(TraceHeaders.X_FORWARDED_FOR) != null) {
                MDC.put(TraceHeaders.X_FORWARDED_FOR, parent.tags().get(TraceHeaders.X_FORWARDED_FOR));
            }
            if (parent.tags().get(TraceHeaders.X_GID_CLIENT_SESSION) != null) {
                MDC.put(TraceHeaders.X_GID_CLIENT_SESSION, parent.tags().get(TraceHeaders.X_GID_CLIENT_SESSION));
            }
        } else {
            MDC.remove(Span.SPAN_ID_NAME);
            MDC.remove(Span.SPAN_EXPORT_NAME);
            MDC.remove(Span.TRACE_ID_NAME);
            MDC.remove(TraceHeaders.X_FORWARDED_FOR);
            MDC.remove(TraceHeaders.X_GID_CLIENT_SESSION);
        }
    }

    private void log(String text, Span span) {
        if (this.nameSkipPattern.matcher(span.getName()).matches()) {
            return;
        }
        this.log.trace(text, span);
    }

}
