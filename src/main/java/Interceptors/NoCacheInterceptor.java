package Interceptors;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;

import java.io.IOException;

public class NoCacheInterceptor implements IClientInterceptor {

    @Override
    public void interceptRequest(IHttpRequest iHttpRequest) {
        System.out.println("Turning Off caching for this request\n");
        iHttpRequest.addHeader("Cache-Control", "no-cache");
    }

    @Override
    public void interceptResponse(IHttpResponse iHttpResponse) throws IOException {

    }
}
