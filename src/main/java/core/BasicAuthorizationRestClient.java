package core;

import exception.AppException;
import logger.AppLogger;
import network.ManagementProtocol;
import org.apache.commons.net.util.Base64;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

public class BasicAuthorizationRestClient {
    private static final AppLogger logger = AppLogger.getLogger(BasicAuthorizationRestClient.class);

    private ManagementProtocol managementProtocol;
    private String controllerIp;
    private String username;
    private String password;
    private int port;

    public BasicAuthorizationRestClient(ManagementProtocol managementProtocol, String controllerIp, String username, String password,
                                        int port) {
        this.managementProtocol = managementProtocol;
        this.controllerIp = controllerIp;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public <T> T sendRequest(String urlRequestSuffix, HttpMethod method, Object requestObject, Class<T> responseClass) throws Exception {
        AuthScope authScope = new AuthScope(controllerIp, port, AuthScope.ANY_REALM);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        RestTemplate restTemplate = RestTemplateFactory.getInstance().createInsecureSslRestTemplate(authScope,
                credentials);

        String url = managementProtocol.toString() + "://" + controllerIp + ":" + port + "/" + urlRequestSuffix;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", createAuthorization());
        headers.add("Content-Type", "application/json");
        @SuppressWarnings("unchecked")
        HttpEntity currentRequestEntity = new HttpEntity(requestObject, headers);
        if (requestObject != null) {
            logger.debug("rest request body is {}", requestObject);
        }

        try {
            @SuppressWarnings("unchecked")
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, method, currentRequestEntity,
                    responseClass);
            return responseEntity.getBody();
        } catch (Throwable e) {
            throw getException(url, e);
        }
    }

    private Exception getException(String url, Throwable e) {
        String message = "error accessing URL " + url;
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException clientErrorException = (HttpStatusCodeException) e;
            message += "\n" + clientErrorException.getResponseBodyAsString();
        }

        return new AppException(message, e);
    }


    private String createAuthorization() throws Exception {
        String authorization = username + ":" + password;
        byte[] encoded = Base64.encodeBase64(authorization.getBytes(Charset.forName("US-ASCII")));
        return "Basic " + new String(encoded);
    }
}