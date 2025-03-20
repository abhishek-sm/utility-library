package com.github.utils.src.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.utils.src.main.constants.RegexPattern;

import lombok.experimental.UtilityClass;

/**
 * Utility class for network operations such as HTTP requests, 
 * IP address validation, port checking, and network diagnostics.
 */
@UtilityClass
public class NetworkUtils {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000; // 5 seconds
    private static final int DEFAULT_READ_TIMEOUT = 10000; // 10 seconds

    /**
     * Sends a GET request to the specified URL.
     *
     * @param urlString The URL to send the request to
     * @return The response as a string
     * @throws IOException If an I/O error occurs
     */
    public static String sendGetRequest(String urlString) throws IOException {
        return sendGetRequest(urlString, null, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Sends a GET request to the specified URL with headers.
     *
     * @param urlString The URL to send the request to
     * @param headers Map of headers to include in the request
     * @return The response as a string
     * @throws IOException If an I/O error occurs
     */
    public static String sendGetRequest(String urlString, Map<String, String> headers) throws IOException {
        return sendGetRequest(urlString, headers, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Sends a GET request to the specified URL with headers and custom timeouts.
     *
     * @param urlString The URL to send the request to
     * @param headers Map of headers to include in the request
     * @param connectionTimeout Connection timeout in milliseconds
     * @param readTimeout Read timeout in milliseconds
     * @return The response as a string
     * @throws IOException If an I/O error occurs
     */
    public static String sendGetRequest(String urlString, Map<String, String> headers,
                                        int connectionTimeout, int readTimeout) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            // Add headers if provided
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            responseCode >= 400
                                    ? connection.getErrorStream()
                                    : connection.getInputStream(),
                            StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Sends a POST request to the specified URL with the given body.
     *
     * @param urlString The URL to send the request to
     * @param requestBody The body of the POST request
     * @param contentType The content type of the request body
     * @return The response as a string
     * @throws IOException If an I/O error occurs
     */
    public static String sendPostRequest(String urlString, String requestBody, String contentType) throws IOException {
        return sendPostRequest(urlString, requestBody, contentType, null, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Sends a POST request to the specified URL with the given body and headers.
     *
     * @param urlString The URL to send the request to
     * @param requestBody The body of the POST request
     * @param contentType The content type of the request body
     * @param headers Map of headers to include in the request
     * @return The response as a string
     * @throws IOException If an I/O error occurs
     */
    public static String sendPostRequest(String urlString, String requestBody,
                                         String contentType, Map<String, String> headers) throws IOException {
        return sendPostRequest(urlString, requestBody, contentType, headers, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * Sends a POST request to the specified URL with the given body, headers, and custom timeouts.
     *
     * @param urlString The URL to send the request to
     * @param requestBody The body of the POST request
     * @param contentType The content type of the request body
     * @param headers Map of headers to include in the request
     * @param connectionTimeout Connection timeout in milliseconds
     * @param readTimeout Read timeout in milliseconds
     * @return The response as a string
     * @throws IOException If an I/O error occurs
     */
    public static String sendPostRequest(String urlString, String requestBody, String contentType,
                                         Map<String, String> headers, int connectionTimeout, int readTimeout) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setDoOutput(true);

            // Set content type
            connection.setRequestProperty("Content-Type", contentType);

            // Add headers if provided
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            // Write request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Read the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            responseCode >= 400
                                    ? connection.getErrorStream()
                                    : connection.getInputStream(),
                            StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Builds a URL with query parameters.
     *
     * @param baseUrl The base URL
     * @param params Map of query parameters
     * @return The constructed URL string
     * @throws IOException If encoding fails
     */
    public static String buildUrlWithQueryParams(String baseUrl, Map<String, String> params) throws IOException {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        StringBuilder result = new StringBuilder(baseUrl);
        boolean first = !baseUrl.contains("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                result.append("?");
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return result.toString();
    }

    /**
     * Checks if a hostname is reachable.
     *
     * @param hostname The hostname to check
     * @param timeout Timeout in milliseconds
     * @return true if the host is reachable, false otherwise
     */
    public static boolean isHostReachable(String hostname, int timeout) {
        try {
            return InetAddress.getByName(hostname).isReachable(timeout);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Checks if a port is open on a given host.
     *
     * @param hostname The hostname to check
     * @param port The port to check
     * @param timeout Timeout in milliseconds
     * @return true if the port is open, false otherwise
     */
    public static boolean isPortOpen(String hostname, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(hostname, port), timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets the local IP addresses of the machine.
     *
     * @param includeLoopback Whether to include loopback addresses
     * @return List of local IP addresses
     * @throws IOException If an I/O error occurs
     */
    public static List<String> getLocalIpAddresses(boolean includeLoopback) throws IOException {
        List<String> addresses = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            // Skip inactive interfaces
            if (!networkInterface.isUp()) {
                continue;
            }

            // Skip loopback interfaces if not included
            if (!includeLoopback && networkInterface.isLoopback()) {
                continue;
            }

            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress address = inetAddresses.nextElement();
                addresses.add(address.getHostAddress());
            }
        }

        return addresses;
    }

    /**
     * Gets the public IP address of the machine using an external service.
     *
     * @return The public IP address or null if it couldn't be determined
     * @throws IOException If an I/O error occurs
     */
    public static String getPublicIpAddress() throws IOException {
        URL url = new URL("https://api.ipify.org");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.readLine();
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Performs a DNS lookup for a hostname.
     *
     * @param hostname The hostname to look up
     * @return Array of IP addresses for the hostname
     * @throws UnknownHostException If the hostname cannot be resolved
     */
    public static String[] dnsLookup(String hostname) throws UnknownHostException {
        InetAddress[] addresses = InetAddress.getAllByName(hostname);
        String[] ipAddresses = new String[addresses.length];

        for (int i = 0; i < addresses.length; i++) {
            ipAddresses[i] = addresses[i].getHostAddress();
        }

        return ipAddresses;
    }

    /**
     * Performs a reverse DNS lookup for an IP address.
     *
     * @param ipAddress The IP address to look up
     * @return The hostname for the IP address
     * @throws UnknownHostException If the IP address cannot be resolved
     */
    public static String reverseDnsLookup(String ipAddress) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ipAddress);
        return address.getHostName();
    }

    /**
     * Validates an IPv4 address.
     *
     * @param ip The IP address to validate
     * @return true if the IP address is a valid IPv4 address, false otherwise
     */
    public static boolean isValidIpv4Address(String ip) {
        return ip != null && RegexPattern.IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * Validates an IPv6 address.
     *
     * @param ip The IP address to validate
     * @return true if the IP address is a valid IPv6 address, false otherwise
     */
    public static boolean isValidIpv6Address(String ip) {
        return ip != null && RegexPattern.IPV6_PATTERN.matcher(ip).matches();
    }

    /**
     * Validates if a string is a valid IP address (IPv4 or IPv6).
     *
     * @param ip The IP address to validate
     * @return true if the IP address is valid, false otherwise
     */
    public static boolean isValidIpAddress(String ip) {
        return isValidIpv4Address(ip) || isValidIpv6Address(ip);
    }

    /**
     * Runs a ping command to a specified host.
     *
     * @param host The host to ping
     * @param count Number of ping packets to send
     * @return Output of the ping command
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the process is interrupted
     */
    public static String ping(String host, int count) throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder = new ProcessBuilder();

        if (os.contains("win")) {
            processBuilder.command("cmd.exe", "/c", "ping", "-n", String.valueOf(count), host);
        } else {
            processBuilder.command("ping", "-c", String.valueOf(count), host);
        }

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        output.append("Exit Code: ").append(exitCode);

        return output.toString();
    }

    /**
     * Runs a traceroute command to a specified host.
     *
     * @param host The host to trace
     * @return Output of the traceroute command
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the process is interrupted
     */
    public static String traceroute(String host) throws IOException, InterruptedException {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder = new ProcessBuilder();

        if (os.contains("win")) {
            processBuilder.command("cmd.exe", "/c", "tracert", host);
        } else {
            processBuilder.command("traceroute", host);
        }

        Process process = processBuilder.start();
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
        }

        int exitCode = process.waitFor();
        output.append("Exit Code: ").append(exitCode);

        return output.toString();
    }

    /**
     * Parses a URL and extracts its components.
     *
     * @param urlString The URL string to parse
     * @return Map containing the URL components (scheme, host, port, path, query, fragment)
     * @throws IOException If the URL is malformed
     */
    public static Map<String, String> parseUrl(String urlString) throws IOException {
        Map<String, String> components = new HashMap<>();
        URI uri = URI.create(urlString);

        components.put("scheme", uri.getScheme());
        components.put("host", uri.getHost());
        components.put("port", uri.getPort() == -1 ? "" : String.valueOf(uri.getPort()));
        components.put("path", uri.getPath());
        components.put("query", uri.getQuery());
        components.put("fragment", uri.getFragment());

        return components;
    }

    /**
     * Downloads a file from a URL to a byte array.
     *
     * @param urlString The URL of the file to download
     * @return The file content as a byte array
     * @throws IOException If an I/O error occurs
     */
    public static byte[] downloadFile(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            int contentLength = connection.getContentLength();
            byte[] buffer;

            if (contentLength > 0) {
                buffer = new byte[contentLength];
            } else {
                buffer = new byte[8192]; // Default buffer size
            }

            int bytesRead;
            int offset = 0;

            try (java.io.InputStream in = connection.getInputStream()) {
                while ((bytesRead = in.read(buffer, offset, buffer.length - offset)) != -1) {
                    offset += bytesRead;
                    if (offset >= buffer.length) {
                        byte[] newBuffer = new byte[buffer.length * 2];
                        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                        buffer = newBuffer;
                    }
                }
            }

            // Create result array of exact size
            byte[] result = new byte[offset];
            System.arraycopy(buffer, 0, result, 0, offset);

            return result;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Gets the MIME type of a URL.
     *
     * @param urlString The URL to check
     * @return The MIME type of the URL's content
     * @throws IOException If an I/O error occurs
     */
    public static String getMimeType(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("HEAD");
            return connection.getContentType();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Checks if a URL exists (returns HTTP 200 OK).
     *
     * @param urlString The URL to check
     * @return true if the URL exists, false otherwise
     */
    public static boolean urlExists(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets network interface information.
     *
     * @return List of maps containing network interface information
     * @throws IOException If an I/O error occurs
     */
    public static List<Map<String, Object>> getNetworkInterfaceInfo() throws IOException {
        List<Map<String, Object>> interfaceInfoList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Map<String, Object> interfaceInfo = new HashMap<>();

            interfaceInfo.put("name", networkInterface.getName());
            interfaceInfo.put("displayName", networkInterface.getDisplayName());
            interfaceInfo.put("isUp", networkInterface.isUp());
            interfaceInfo.put("isLoopback", networkInterface.isLoopback());
            interfaceInfo.put("isPointToPoint", networkInterface.isPointToPoint());
            interfaceInfo.put("isVirtual", networkInterface.isVirtual());
            interfaceInfo.put("supportsMulticast", networkInterface.supportsMulticast());

            try {
                interfaceInfo.put("hardwareAddress",
                        networkInterface.getHardwareAddress() != null
                                ? formatMacAddress(networkInterface.getHardwareAddress())
                                : null);
            } catch (IOException e) {
                interfaceInfo.put("hardwareAddress", null);
            }

            interfaceInfo.put("MTU", networkInterface.getMTU());

            List<String> addresses = new ArrayList<>();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                addresses.add(inetAddresses.nextElement().getHostAddress());
            }
            interfaceInfo.put("addresses", addresses);

            interfaceInfoList.add(interfaceInfo);
        }

        return interfaceInfoList;
    }

    /**
     * Formats a MAC address byte array to a string.
     *
     * @param mac The MAC address byte array
     * @return The formatted MAC address string
     */
    private static String formatMacAddress(byte[] mac) {
        if (mac == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X", mac[i]));
            if (i < mac.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }

    /**
     * Checks if the system has internet connectivity.
     *
     * @return true if the system has internet connectivity, false otherwise
     */
    public static boolean hasInternetConnectivity() {
        String[] hostnames = {"google.com", "cloudflare.com", "amazon.com"};

        for (String hostname : hostnames) {
            if (isHostReachable(hostname, 3000)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the HTTP response code for a URL.
     *
     * @param urlString The URL to check
     * @return The HTTP response code
     * @throws IOException If an I/O error occurs
     */
    public static int getHttpResponseCode(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            return connection.getResponseCode();
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Gets HTTP response headers for a URL.
     *
     * @param urlString The URL to check
     * @return Map of HTTP response headers
     * @throws IOException If an I/O error occurs
     */
    public static Map<String, List<String>> getHttpHeaders(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(DEFAULT_READ_TIMEOUT);

            return connection.getHeaderFields();
        } finally {
            connection.disconnect();
        }
    }
}
