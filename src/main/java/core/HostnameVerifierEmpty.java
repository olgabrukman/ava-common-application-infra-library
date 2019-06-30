package core;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class HostnameVerifierEmpty implements HostnameVerifier {
    public boolean verify(String arg0, SSLSession arg1) {
        return true;
    }
}
