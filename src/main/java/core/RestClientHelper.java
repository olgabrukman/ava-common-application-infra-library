package core;


import exception.AppException;
import logger.AppLogger;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.javatuples.Pair;
import org.javatuples.Quintet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import resource.MessageApi;

import java.net.URI;
import java.util.HashMap;

public abstract class RestClientHelper {
    private static final AppLogger logger = AppLogger.getLogger(RestClientHelper.class);

    protected static HashMap<Quintet<String, String, Integer, String, String>, Pair<String, Long>> securityTokens =
            new HashMap<>();

    protected final String ip;
    protected final int port;
    protected final String username;
    protected final String password;
    protected final String contentType;
    protected final Quintet<String, String, Integer, String, String> quintet;

    public RestClientHelper(String ip, int port, String username, String password, String contentType) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.contentType = contentType;
        this.quintet = new Quintet<>(this.getClass().getName(), ip, port, username, password);
    }

    public boolean isAuthenticated() {
        return securityTokens.get(quintet) != null;
    }

    public long getTokenExpirationTime() {
        long tokenExpirationTime = -1;
        if (isAuthenticated()) {
            tokenExpirationTime = securityTokens.get(quintet).getValue1();
        }
        return tokenExpirationTime;
    }

    public String getSecurityToken() throws Exception {
        if ((!isAuthenticated()) || (System.currentTimeMillis() > getTokenExpirationTime())) {
            logger.debug("getting security token");
            generateSecurityToken();
        }
        return securityTokens.get(quintet).getValue0();
    }

    public abstract void generateSecurityToken() throws Exception;

    public <T> T sendRequestHelper(String urlRequestSuffix, HttpMethod method, Object requestObject,
                                   Class<T> responseClass, String authorizationKey, String authorizationValue)
            throws Exception {
        AuthScope authScope = new AuthScope(ip, port, AuthScope.ANY_REALM);
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        RestTemplate restTemplate = RestTemplateFactory.getInstance().createInsecureSslRestTemplate(authScope,
                credentials);

        String url = "https://" + ip + ":" + port + "/" + urlRequestSuffix;

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(authorizationKey, authorizationValue);
        headers.add("Content-Type", contentType);
        @SuppressWarnings("unchecked")
        HttpEntity currentRequestEntity = new HttpEntity(requestObject, headers);
        if (requestObject != null) {
            logger.debug("rest request body is {}", requestObject);
        }

        boolean retriedAuthenticating = false;
        while (true) {
            try {
                @SuppressWarnings("unchecked")
                ResponseEntity<T> responseEntity = restTemplate.exchange(new URI(url), method, currentRequestEntity,
                        responseClass);
                return responseEntity.getBody();
            } catch (Throwable e) {
                Exception exception = getException(url, e);
                if (!exception.getMessage().contains("expired access token")) {
                    throw exception;
                }
                if (retriedAuthenticating) {
                    throw exception;
                }

                // change of time on machines can cause expiration time to be incorrect - try again
                String message = MessageApi.getResource("app00298",
                        "URL", url);
                logger.warn(message, exception);
                retriedAuthenticating = true;
                generateSecurityToken();
            }
        }
    }

    private Exception getException(String url, Throwable e) {
        String message = "error accessing URL " + url;
        if (e instanceof HttpStatusCodeException) {
            HttpStatusCodeException clientErrorException = (HttpStatusCodeException) e;
            message += "\n" + clientErrorException.getResponseBodyAsString();
        }

        message = message.replaceAll("\\\\n", "\n");
        return new AppException(message, e);
    }
}