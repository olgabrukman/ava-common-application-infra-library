package core;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpEntityEnclosingDeleteRequest extends HttpEntityEnclosingRequestBase {
    public HttpEntityEnclosingDeleteRequest(final URI uri) {
        super();
        setURI(uri);
    }

    @Override
    public String getMethod() {
        return "DELETE";
    }
}