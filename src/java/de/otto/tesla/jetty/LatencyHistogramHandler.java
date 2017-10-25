package de.otto.tesla.jetty;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.lang.String.valueOf;

public class LatencyHistogramHandler extends HandlerWrapper {
    private final CollectorRegistry registry;
    private Histogram httpHistogram;


    public LatencyHistogramHandler(CollectorRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void doStart() throws Exception {
        Histogram.Builder builder = new Histogram.Builder();
        this.httpHistogram = builder
                .name("jetty_http_duration_in_s").
                        buckets(0.001, 0.005, 0.01, 0.05, 0.1, 0.5, 1).
                        help("Request latencies as perceived by jetty.")
                .labelNames("method", "rc")
                .create();
        httpHistogram.register(registry);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            super.handle(target, baseRequest, request, response);
        } finally {
            double duration = (System.currentTimeMillis() - baseRequest.getTimeStamp()) / 1000.0;
            httpHistogram.labels(request.getMethod(), valueOf(response.getStatus())).observe(duration);
        }
    }

}