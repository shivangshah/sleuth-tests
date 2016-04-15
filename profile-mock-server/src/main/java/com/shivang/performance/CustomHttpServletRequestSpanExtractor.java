package com.shivang.performance;

import java.util.Random;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Span.SpanBuilder;
import org.springframework.cloud.sleuth.SpanExtractor;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UrlPathHelper;

class CustomHttpServletRequestSpanExtractor implements SpanExtractor<HttpServletRequest> {

    private static final String HTTP_COMPONENT = "http";
    private final Random random;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public CustomHttpServletRequestSpanExtractor(Random random) {
        this.random = random;
    }

    @Override
    public Span joinTrace(HttpServletRequest carrier) {
        boolean isRemote = false;
        if(carrier.getHeader(Span.TRACE_ID_NAME) != null)
        {
            isRemote = true;
        }
        String uri = this.urlPathHelper.getPathWithinApplication(carrier);
        long traceId = carrier.getHeader(Span.TRACE_ID_NAME) != null
                ? Span.hexToId(carrier.getHeader(Span.TRACE_ID_NAME))
                : this.random.nextLong();
        long spanId = carrier.getHeader(Span.SPAN_ID_NAME) != null
                ? Span.hexToId(carrier.getHeader(Span.SPAN_ID_NAME))
                : this.random.nextLong();
        return buildParentSpan(carrier, uri, traceId, spanId, isRemote);
    }

    private Span buildParentSpan(HttpServletRequest carrier, String uri,
            long traceId, long spanId, boolean isRemote) {
        SpanBuilder span = Span.builder().traceId(traceId).spanId(spanId);
        String processId = carrier.getHeader(Span.PROCESS_ID_NAME);
        String parentName = carrier.getHeader(Span.SPAN_NAME_NAME);
        String trueClientIp = carrier.getHeader(TraceHeaders.X_FORWARDED_FOR);
        String trueClientSession = carrier.getHeader(TraceHeaders.X_GID_CLIENT_SESSION);
        if (trueClientIp != null) {
            span.tag(TraceHeaders.X_FORWARDED_FOR, trueClientIp);
        }
        if (trueClientSession != null) {
            span.tag(TraceHeaders.X_GID_CLIENT_SESSION, trueClientSession);
        }

        if (StringUtils.hasText(parentName)) {
            span.name(parentName);
        } else {
            span.name(HTTP_COMPONENT + ":" + "/parent" + uri);
        }
        if (StringUtils.hasText(processId)) {
            span.processId(processId);
        }
        if (carrier.getHeader(Span.PARENT_ID_NAME) != null) {
            span.parent(Span
                    .hexToId(carrier.getHeader(Span.PARENT_ID_NAME)));
        }
        span.remote(isRemote);
        return span.build();
    }
}
