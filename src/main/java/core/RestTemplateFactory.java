package core;


import config.Config;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.security.SecureRandom;
import java.util.List;


public class RestTemplateFactory {

    private static RestTemplateFactory INSTANCE = new RestTemplateFactory();


    private RestTemplateFactory() {
    }

    static public RestTemplateFactory getInstance() {
        return INSTANCE;
    }


    public RestTemplate createInsecureSslRestTemplate(AuthScope authScope, UsernamePasswordCredentials credentials) throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifierEmpty());
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(authScope, credentials);

        TrustManager[] trustAllCerts = {new X509TrustManagerAll()};
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        int timeout = Config.getInstance().getRestTimeout() * 1000;
        RequestConfig.Builder custom = RequestConfig.custom();
        custom.setConnectTimeout(timeout);
        custom.setConnectionRequestTimeout(timeout);
        custom.setSocketTimeout(timeout);
        RequestConfig builder = custom.build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setHostnameVerifier(new X509HostnameEmptyVerifier());
        httpClientBuilder.setSslcontext(sc);
        httpClientBuilder.setDefaultRequestConfig(builder);
        HttpClient httpClient = httpClientBuilder.build();

        HttpComponentsClientHttpRequestFactory factory = new EnhancedHttpComponentsClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);

        disableWriteAcceptCharset(restTemplate);
        return restTemplate;
    }

    private void disableWriteAcceptCharset(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> c = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> mc : c) {
            if (mc instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter mcc = (StringHttpMessageConverter) mc;
                mcc.setWriteAcceptCharset(false);
            }
        }
    }


}
