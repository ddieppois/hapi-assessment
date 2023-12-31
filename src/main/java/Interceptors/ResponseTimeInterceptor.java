package Interceptors;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class ResponseTimeInterceptor implements IClientInterceptor {
    private List<Long> responseTimes;


    private StopWatch requestStopWatch;

    public ResponseTimeInterceptor() {
        responseTimes = new ArrayList<>();
    }
    //Constructor for testing purposes
    public ResponseTimeInterceptor(List<Long> responseTimes) {
        this.responseTimes = responseTimes;
    }

    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        requestStopWatch = new StopWatch();
    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) {
        // Add the response time to the list and restart the watch
        responseTimes.add(requestStopWatch.getMillisAndRestart());
    }

    public double displayAverageResponseTime() {
        // Calculate the average response time
        long totalResponseTime = 0;
        for (Long time : responseTimes) {
            totalResponseTime += time;
        }
        double averageResponseTime = (double) totalResponseTime / responseTimes.size();

        // Print the average response time for all requests so far
        System.out.println("Average Response Time for " + responseTimes.size() + " requests: " + averageResponseTime + "ms\n");
        return averageResponseTime;
    }

    public void resetResponseTimes() {
        responseTimes = new ArrayList<>();
    }

    public List<Long> getResponseTimes() {
        return responseTimes;
    }

    public StopWatch getRequestStopWatch() {
        return requestStopWatch;
    }

}
