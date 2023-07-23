package Interceptors;

import ca.uhn.fhir.rest.client.api.IHttpRequest;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class NoCacheInterceptorTest {

    @Test
    public void testInterceptRequest() {
        NoCacheInterceptor noCacheInterceptor = new NoCacheInterceptor();
        IHttpRequest mockRequest = mock(IHttpRequest.class);
        noCacheInterceptor.interceptRequest(mockRequest);
        verify(mockRequest).addHeader("Cache-Control", "no-cache");
    }
}