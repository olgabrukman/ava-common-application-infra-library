package core;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.net.URI;

public class EnhancedHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
    public EnhancedHttpComponentsClientHttpRequestFactory(HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
        if (HttpMethod.DELETE == httpMethod) {
            //to add delete body support for restTemplate
            return new HttpEntityEnclosingDeleteRequest(uri);
        }
        return super.createHttpUriRequest(httpMethod, uri);
    }
}