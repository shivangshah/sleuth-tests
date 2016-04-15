package org.springframework.cloud.sleuth.trace;

import java.util.Random;
import java.util.concurrent.Callable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanNamer;
import org.springframework.cloud.sleuth.SpanReporter;
import org.springframework.cloud.sleuth.TraceKeys;
import org.springframework.cloud.sleuth.log.SpanLogger;

/**
 *
 * @author Shivang Shah
 */
public class CustomTracer extends DefaultTracer {

    @Autowired
    private TraceKeys traceKeys;

    public CustomTracer(Sampler defaultSampler, Random random,
            SpanNamer spanNamer, SpanLogger spanLogger, SpanReporter spanReporter) {
        super(defaultSampler, random, spanNamer, spanLogger, spanReporter);
    }

    private void copyTagsFromParentToChild(Span parent, Span child) {
        if (parent == null) {
            return;
        }
        for (String header : traceKeys.getHttp().getHeaders()) {
            child.tag(header, parent.tags().get(header));
        }
    }

    @Override
    protected Span createChild(Span parent, String name) {
        Span child = super.createChild(parent, name);
        copyTagsFromParentToChild(parent, child);
        return child;
    }

}
