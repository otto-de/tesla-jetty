package de.otto.tesla.jetty;

import io.prometheus.client.Collector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.jetty.server.handler.StatisticsHandler;

//This statisticsCollector is based on https://github.com/prometheus/client_java but modified for jetty-12
public class JettyStatisticsCollector extends Collector {
    private final StatisticsHandler statisticsHandler;
    private static final List<String> EMPTY_LIST = new ArrayList<String>();

    public JettyStatisticsCollector(StatisticsHandler statisticsHandler) {
        this.statisticsHandler = statisticsHandler;
    }


    @Override
    public List<MetricFamilySamples> collect() {

        return Arrays.asList(
                buildCounter("jetty_requests_total", "Number of requests", statisticsHandler.getRequestTotal()),
                buildGauge("jetty_requests_active", "Number of requests currently active", statisticsHandler.getRequestsActive()),
                buildGauge("jetty_requests_active_max", "Maximum number of requests that have been active at once", statisticsHandler.getRequestsActiveMax()),
                buildGauge("jetty_request_time_max_seconds", "Maximum time spent handling requests", statisticsHandler.getRequestTimeMax() / 1000.0),
                buildCounter("jetty_request_time_seconds_total", "Total time spent in all request handling", statisticsHandler.getRequestTimeTotal() / 1000.0),
                buildCounter("jetty_bytes_written_total", "Total bytes written", statisticsHandler.getBytesWritten()),
                buildCounter("jetty_bytes_read_total", "Total bytes read", statisticsHandler.getBytesRead()),
                buildStatusCounter()
        );
    }

    private static MetricFamilySamples buildGauge(String name, String help, double value) {
        return new MetricFamilySamples(
                name,
                Type.GAUGE,
                help,
                Collections.singletonList(new MetricFamilySamples.Sample(name, EMPTY_LIST, EMPTY_LIST, value)));
    }

    private static MetricFamilySamples buildCounter(String name, String help, double value) {
        return new MetricFamilySamples(
                name,
                Type.COUNTER,
                help,
                Collections.singletonList(new MetricFamilySamples.Sample(name, EMPTY_LIST, EMPTY_LIST, value)));
    }

    private MetricFamilySamples buildStatusCounter() {
        String name = "jetty_responses_total";
        return new MetricFamilySamples(
                name,
                Type.COUNTER,
                "Number of requests with response status",
                Arrays.asList(
                        buildStatusSample(name, "1xx", statisticsHandler.getResponses1xx()),
                        buildStatusSample(name, "2xx", statisticsHandler.getResponses2xx()),
                        buildStatusSample(name, "3xx", statisticsHandler.getResponses3xx()),
                        buildStatusSample(name, "4xx", statisticsHandler.getResponses4xx()),
                        buildStatusSample(name, "5xx", statisticsHandler.getResponses5xx())
                )
        );
    }

    private static MetricFamilySamples.Sample buildStatusSample(String name, String status, double value) {
        return new MetricFamilySamples.Sample(
                name,
                Collections.singletonList("code"),
                Collections.singletonList(status),
                value);
    }
}
