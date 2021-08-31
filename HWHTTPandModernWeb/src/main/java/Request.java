import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;
    static Map<String, String> queryParams = new HashMap<>();

    private Request(String method, String path, Map<String, String> headers, InputStream in) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                '}';
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    public static Request fromInputStream(InputStream inputStream) throws IOException {
        final var in = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            throw new IOException("Request error");
        }

        var method = parts[0];
        var path = parts[1];

        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = in.readLine()).equals("\r\n")) {
            var elementIndex = line.indexOf(":");
            var headerName = line.substring(0, elementIndex);
            var headerValue = line.substring(elementIndex + 2);

            headers.put(headerName, headerValue);

        }
        return new Request(method, path, headers, inputStream);
    }

    public String getQueryParam(String paramName) {
        List<NameValuePair> queryParamsList = URLEncodedUtils.parse(URI.create(path), "UTF-8");
        return queryParamsList.stream()
                .filter(x -> x.getName().equalsIgnoreCase(paramName))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse("");
    }

    public Map<String, String> getQueryParams() {
        List<NameValuePair> queryParamsList = URLEncodedUtils.parse(URI.create(path), "UTF-8");
        for (NameValuePair param : queryParamsList) {
            String name = param.getName();
            String value = param.getValue();
            queryParams.put(name, value);
        }
        return queryParams;
    }
}
