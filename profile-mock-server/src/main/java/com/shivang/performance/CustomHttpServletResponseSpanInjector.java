package com.shivang.performance;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanInjector;
import org.springframework.cloud.sleuth.TraceKeys;

class CustomHttpServletResponseSpanInjector implements SpanInjector<HttpServletResponse> {

    @Autowired
    private TraceKeys traceKeys;

    @Override
    public void inject(Span span, HttpServletResponse carrier) {
        if (span == null) {
            return;
        }
        if (!carrier.containsHeader(Span.SPAN_ID_NAME)) {
            carrier.addHeader(Span.SPAN_ID_NAME, Span.idToHex(span.getSpanId()));
            carrier.addHeader(Span.TRACE_ID_NAME, Span.idToHex(span.getTraceId()));
            for (String header : traceKeys.getHttp().getHeaders()) {
                carrier.addHeader(header, span.tags()
                        .get(traceKeys.getHttp().getPrefix() + header));
            }
        }
    }
}
