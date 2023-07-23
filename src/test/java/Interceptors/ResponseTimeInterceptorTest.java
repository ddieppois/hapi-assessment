package Interceptors;

import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

public class ResponseTimeInterceptorTest extends TestCase {

    private ResponseTimeInterceptor responseTimeInterceptor;

    @BeforeEach
    public void setUp() throws Exception {
        responseTimeInterceptor = new ResponseTimeInterceptor(getTestResponseTimes());
    }
    @Test
    public void testInterceptRequest() {
        assertNull(this.responseTimeInterceptor.getRequestStopWatch());
        this.responseTimeInterceptor.interceptRequest(mock(IHttpRequest.class));
        assertNotNull(this.responseTimeInterceptor.getRequestStopWatch());
    }

    @Test
    public void testInterceptResponse() {
        this.responseTimeInterceptor = new ResponseTimeInterceptor(new ArrayList<>());
        assertEquals(0, this.responseTimeInterceptor.getResponseTimes().size());
        this.responseTimeInterceptor.interceptRequest(mock(IHttpRequest.class));
        this.responseTimeInterceptor.interceptResponse(mock(IHttpResponse.class));
        assertEquals(1, this.responseTimeInterceptor.getResponseTimes().size());
    }

    @Test
    public void testDisplayAverageResponseTime() {
        assertEquals(3.0, this.responseTimeInterceptor.displayAverageResponseTime());
    }

    @Test
    public void testResetResponseTimes() {
        this.responseTimeInterceptor.resetResponseTimes();
        assertEquals(0, this.responseTimeInterceptor.getResponseTimes().size());
    }

    private List<Long> getTestResponseTimes() {
        return new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L, 5L));
    }
}