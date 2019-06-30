package core;


import logger.AppLogger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final AppLogger logger = AppLogger.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("request URL {}", httpRequest.getURI());
            logger.debug("request headers {}", httpRequest.getHeaders());
            logger.debug("request data {}", new String(bytes));
        }
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}