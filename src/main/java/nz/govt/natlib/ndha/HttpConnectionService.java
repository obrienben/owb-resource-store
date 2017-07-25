package nz.govt.natlib.ndha;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * This class provides a service for querying a simple web service, establishing
 * an HTTP connection.
 *
 * @author Ben O'Brien
 */
public class HttpConnectionService {
    private static final Logger logger = LogManager.getLogger(HttpConnectionService.class);

//    private String hostName = "http://localhost:8080/OWResourceStore/";
    private String hostName = "http://slboprtest.natlib.govt.nz/OWResourceStore/";
    private String proxyHost = null;
    private int proxyPort;
    private String errorMessage = "Error executing query. Please try again, and if the issue persists please contact Support";

    public HttpConnectionService(String hostName) {
        this.hostName = hostName;
    }


    /**
     * Executes an GET request to specified URL.
     * Builds default query string and appends any additional. Sets any additional
     * header request properties.
     *
     * @param urlRequest    url string to append onto hostname
     * @return                              string response
     */
    public String get(String urlRequest) throws Exception {

        StringBuilder responseData = new StringBuilder();
        StringBuilder urlBuilder = new StringBuilder();
        // Build and append query string
        urlBuilder = new StringBuilder(hostName);
        if(urlRequest != null){
            urlBuilder.append(urlRequest);
        }
        URL url = new URL(urlBuilder.toString());

        // Establish HTTP connection
        HttpURLConnection con = getConnection(url);
        con.setRequestMethod("GET");
//        con.setFixedLengthStreamingMode(requestBody.length);
//        con.setDoOutput(true);

//        logger.info("OpenWayback VPP - HttpConnection post to: " + urlBuilder.toString());

//        OutputStream out = con.getOutputStream();
//        out.write(requestBody);

        InputStream ins;
        if (con.getResponseCode() >= 400) {
            InputStreamReader isr = new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(isr);

            String inputLine;
            while ((inputLine = in.readLine()) != null){
                responseData.append(inputLine);
            }
            in.close();
            con.disconnect();
            logger.error(responseData.toString());
            return null;
        }

        // Read response from successful request
        InputStreamReader isr = new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(isr);

        String inputLine;
        while ((inputLine = in.readLine()) != null)
        {
//              logger.info("OpenWayback VPP - HttpConnection response: " + inputLine);
            responseData.append(inputLine);
        }
        in.close();
        con.disconnect();
        return responseData.toString();
    }

    /**
     * Establishes HTTP connection to a URL.
     * If proxy properties are provided then set proxy details to
     * be used by HTTPS connection.
     *
     * @param url URL to connect
     * @return a new Https Connection
     * @throws IOException
     */
    private HttpURLConnection getConnection(URL url) throws IOException {
        HttpURLConnection con;
        if(proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0){
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
            con = (HttpURLConnection)url.openConnection(proxy);
        } else {
            con = (HttpURLConnection)url.openConnection();
        }

        return con;
    }

    /**
     * Sets the hostname string.
     *
     * @param hostName a URL hostname
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Sets the Proxy Server hostname. Used when establishing an HTTP(S)
     * connection.
     *
     * @param proxyHost a URL for the Proxy Server
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * Sets the Proxy Server port. Used when establishing an HTTP(S)
     * connection.
     *
     * @param proxyPort a port number for the Proxy Server
     */
    public void setProxyPort(String proxyPort) {
        if(!proxyPort.equals("")){
            this.proxyPort = Integer.parseInt(proxyPort);
        }
    }

    /**
     * Sets the default error message used when there is an error connecting to
     * the RESTful service.
     *
     * @param errorMessage an error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
