package com.github.utils.src.main.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.utils.src.main.exception.HttpUtilsException;

import lombok.Data;

/**
 * A utility class for making HTTP requests with support for synchronous and asynchronous operations.
 * Provides methods for common HTTP verbs (GET, POST, PUT, DELETE, PATCH) with JSON serialization/deserialization.
 * 
 * TODO: Refactor this class to use @UtilityClass in the future to make it a true utility class.
 */
@Data
public class HttpUtils {

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final Duration timeout;
    
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final String CONTENT_TYPE_KEY = "Content-Type";

    /**
     * Creates a new HttpUtils instance with default configuration.
     */
    public HttpUtils() {
        this(DEFAULT_TIMEOUT);
    }

    /**
     * Creates a new HttpUtils instance with the specified timeout.
     *
     * @param timeout the timeout for all requests
     */
    public HttpUtils(Duration timeout) {
        this.timeout = timeout;
        this.client = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Creates a new HttpUtils instance with custom configuration.
     *
     * @param clientBuilder custom HttpClient.Builder to use
     * @param objectMapper custom ObjectMapper to use
     * @param timeout default timeout for requests
     */
    public HttpUtils(HttpClient.Builder clientBuilder, ObjectMapper objectMapper, Duration timeout) {
        this.timeout = timeout;
        this.client = clientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a GET request and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String get(String url) throws HttpUtilsException {
        return getWithHeaders(url, null);
    }

    /**
     * Sends a GET request with headers and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param headers the headers to include with the request
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String getWithHeaders(String url, Map<String, String> headers) throws HttpUtilsException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createUri(url))
                .GET()
                .timeout(timeout);

        addHeaders(requestBuilder, headers);

        return sendRequest(requestBuilder.build());
    }

    /**
     * Sends a GET request and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T get(String url, Class<T> responseType) throws HttpUtilsException {
        return get(url, null, responseType);
    }

    /**
     * Sends a GET request with headers and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param headers the headers to include with the request
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T get(String url, Map<String, String> headers, Class<T> responseType) throws HttpUtilsException {
        String response = getWithHeaders(url, headers);
        return deserialize(response, responseType);
    }

    /**
     * Sends a GET request with headers and deserializes the response to the specified type reference.
     * Useful for generic types like List<MyClass>.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param headers the headers to include with the request
     * @param typeReference the type reference to deserialize to
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T get(String url, Map<String, String> headers, TypeReference<T> typeReference) throws HttpUtilsException {
        String response = getWithHeaders(url, headers);
        return deserialize(response, typeReference);
    }

    /**
     * Sends a POST request with a JSON body and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String post(String url, Object body) throws HttpUtilsException {
        return postWithBodyAndHeaders(url, body, null);
    }

    /**
     * Sends a POST request with a JSON body and headers and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param headers the headers to include with the request
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String postWithBodyAndHeaders(String url, Object body, Map<String, String> headers) throws HttpUtilsException {
        try {
            String jsonBody = (body instanceof String) ? (String) body : objectMapper.writeValueAsString(body);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(createUri(url))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(timeout);

            Map<String, String> allHeaders = new HashMap<>();
            if (headers != null) {
                allHeaders.putAll(headers);
            }

            // Only add Content-Type if not already specified
            if (!allHeaders.containsKey(CONTENT_TYPE_KEY)) {
                allHeaders.put(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
            }

            addHeaders(requestBuilder, allHeaders);

            return sendRequest(requestBuilder.build());
        } catch (JsonProcessingException e) {
            throw new HttpUtilsException("Failed to serialize request body", e);
        }
    }

    /**
     * Sends a POST request with a JSON body and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T post(String url, Object body, Class<T> responseType) throws HttpUtilsException {
        return post(url, body, null, responseType);
    }

    /**
     * Sends a POST request with a JSON body and headers and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param headers the headers to include with the request
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T post(String url, Object body, Map<String, String> headers, Class<T> responseType) throws HttpUtilsException {
        String response = postWithBodyAndHeaders(url, body, headers);
        return deserialize(response, responseType);
    }

    /**
     * Sends a PUT request with a JSON body and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String put(String url, Object body) throws HttpUtilsException {
        return putWithBodyAndHeaders(url, body, null);
    }

    /**
     * Sends a PUT request with a JSON body and headers and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param headers the headers to include with the request
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String putWithBodyAndHeaders(String url, Object body, Map<String, String> headers) throws HttpUtilsException {
        try {
            String jsonBody = (body instanceof String) ? (String) body : objectMapper.writeValueAsString(body);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(createUri(url))
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(timeout);

            Map<String, String> allHeaders = new HashMap<>();
            if (headers != null) {
                allHeaders.putAll(headers);
            }

            // Only add Content-Type if not already specified
            if (!allHeaders.containsKey(CONTENT_TYPE_KEY)) {
                allHeaders.put(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
            }

            addHeaders(requestBuilder, allHeaders);

            return sendRequest(requestBuilder.build());
        } catch (JsonProcessingException e) {
            throw new HttpUtilsException("Failed to serialize request body", e);
        }
    }

    /**
     * Sends a PUT request with a JSON body and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T put(String url, Object body, Class<T> responseType) throws HttpUtilsException {
        return put(url, body, null, responseType);
    }

    /**
     * Sends a PUT request with a JSON body and headers and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param headers the headers to include with the request
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T put(String url, Object body, Map<String, String> headers, Class<T> responseType) throws HttpUtilsException {
        String response = putWithBodyAndHeaders(url, body, headers);
        return deserialize(response, responseType);
    }

    /**
     * Sends a DELETE request and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String delete(String url) throws HttpUtilsException {
        return deleteWithHeaders(url, null);
    }

    /**
     * Sends a DELETE request with headers and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param headers the headers to include with the request
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String deleteWithHeaders(String url, Map<String, String> headers) throws HttpUtilsException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createUri(url))
                .DELETE()
                .timeout(timeout);

        addHeaders(requestBuilder, headers);

        return sendRequest(requestBuilder.build());
    }

    /**
     * Sends a DELETE request and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T delete(String url, Class<T> responseType) throws HttpUtilsException {
        return delete(url, null, responseType);
    }

    /**
     * Sends a DELETE request with headers and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param headers the headers to include with the request
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T delete(String url, Map<String, String> headers, Class<T> responseType) throws HttpUtilsException {
        String response = deleteWithHeaders(url, headers);
        return deserialize(response, responseType);
    }

    /**
     * Sends a PATCH request with a JSON body and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String patch(String url, Object body) throws HttpUtilsException {
        return patchWithBodyAndHeaders(url, body, null);
    }

    /**
     * Sends a PATCH request with a JSON body and headers and returns the response as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param headers the headers to include with the request
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    public String patchWithBodyAndHeaders(String url, Object body, Map<String, String> headers) throws HttpUtilsException {
        try {
            String jsonBody = (body instanceof String) ? (String) body : objectMapper.writeValueAsString(body);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(createUri(url))
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(timeout);

            Map<String, String> allHeaders = new HashMap<>();
            if (headers != null) {
                allHeaders.putAll(headers);
            }

            // Only add Content-Type if not already specified
            if (!allHeaders.containsKey(CONTENT_TYPE_KEY)) {
                allHeaders.put(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
            }

            addHeaders(requestBuilder, allHeaders);

            return sendRequest(requestBuilder.build());
        } catch (JsonProcessingException e) {
            throw new HttpUtilsException("Failed to serialize request body", e);
        }
    }

    /**
     * Sends a PATCH request with a JSON body and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T patch(String url, Object body, Class<T> responseType) throws HttpUtilsException {
        return patch(url, body, null, responseType);
    }

    /**
     * Sends a PATCH request with a JSON body and headers and deserializes the response to the specified type.
     *
     * @param <T> the type to deserialize to
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param headers the headers to include with the request
     * @param responseType the class of the response type
     * @return the deserialized response
     * @throws HttpUtilsException if an error occurs
     */
    public <T> T patch(String url, Object body, Map<String, String> headers, Class<T> responseType) throws HttpUtilsException {
        String response = patchWithBodyAndHeaders(url, body, headers);
        return deserialize(response, responseType);
    }

    /**
     * Downloads a file from the specified URL and saves it to the specified destination path.
     *
     * @param fileUrl the URL of the file to download
     * @param destinationPath the path to save the file to
     * @throws HttpUtilsException if an error occurs
     */
    public void downloadFile(String fileUrl, Path destinationPath) throws HttpUtilsException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(createUri(fileUrl))
                .GET()
                .timeout(Duration.ofMinutes(2))
                .build();

        try {
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (isSuccessful(response.statusCode())) {
                Files.createDirectories(destinationPath.getParent());
                Files.write(destinationPath, response.body(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } else {
                throw new HttpUtilsException("Failed to download file. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpUtilsException("Failed to download file", e);
        }
    }

    /**
     * Downloads a file from the specified URL and saves it to the specified destination path string.
     *
     * @param fileUrl the URL of the file to download
     * @param destinationPath the path to save the file to as a string
     * @throws HttpUtilsException if an error occurs
     */
    public void downloadFile(String fileUrl, String destinationPath) throws HttpUtilsException {
        downloadFile(fileUrl, Path.of(destinationPath));
    }

    /**
     * Downloads a file asynchronously from the specified URL and saves it to the specified destination path.
     *
     * @param fileUrl the URL of the file to download
     * @param destinationPath the path to save the file to
     * @param progressListener a callback function that is called with progress updates (0.0 to 1.0)
     * @return a CompletableFuture that completes when the download is finished
     */
    public CompletableFuture<Path> downloadFileAsync(String fileUrl, Path destinationPath, Consumer<Double> progressListener) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(createUri(fileUrl))
                .GET()
                .timeout(Duration.ofMinutes(2))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> {
                    if (!isSuccessful(response.statusCode())) {
                        throw new HttpUtilsException("Failed to download file. Status code: " + response.statusCode());
                    }
                    return response.body();
                })
                .thenApply(bytes -> {
                    try {
                        Files.createDirectories(destinationPath.getParent());
                        Files.write(destinationPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                        if (progressListener != null) {
                            progressListener.accept(1.0); // Download complete
                        }
                        return destinationPath;
                    } catch (IOException e) {
                        throw new HttpUtilsException("Failed to write downloaded file", e);
                    }
                });
    }

    /**
     * Sends a GET request asynchronously and returns a CompletableFuture with the response body as a string.
     *
     * @param url the URL to send the request to
     * @return a CompletableFuture that completes with the response body
     */
    public CompletableFuture<String> getAsync(String url) {
        return getAsync(url, null);
    }

    /**
     * Sends a GET request asynchronously with headers and returns a CompletableFuture with the response body as a string.
     *
     * @param url the URL to send the request to
     * @param headers the headers to include with the request
     * @return a CompletableFuture that completes with the response body
     */
    public CompletableFuture<String> getAsync(String url, Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(createUri(url))
                .GET()
                .timeout(timeout);

        addHeaders(requestBuilder, headers);

        return sendRequestAsync(requestBuilder.build());
    }

    /**
     * Sends a POST request asynchronously with a JSON body and returns a CompletableFuture with the response body as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @return a CompletableFuture that completes with the response body
     */
    public CompletableFuture<String> postAsync(String url, Object body) {
        return postAsync(url, body, null);
    }

    /**
     * Sends a POST request asynchronously with a JSON body and headers and returns a CompletableFuture with the response body as a string.
     *
     * @param url the URL to send the request to
     * @param body the object to serialize as the request body
     * @param headers the headers to include with the request
     * @return a CompletableFuture that completes with the response body
     */
    public CompletableFuture<String> postAsync(String url, Object body, Map<String, String> headers) {
        try {
            String jsonBody = (body instanceof String) ? (String) body : objectMapper.writeValueAsString(body);

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(createUri(url))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(timeout);

            Map<String, String> allHeaders = new HashMap<>();
            if (headers != null) {
                allHeaders.putAll(headers);
            }

            // Only add Content-Type if not already specified
            if (!allHeaders.containsKey(CONTENT_TYPE_KEY)) {
                allHeaders.put(CONTENT_TYPE_KEY, DEFAULT_CONTENT_TYPE);
            }

            addHeaders(requestBuilder, allHeaders);

            return sendRequestAsync(requestBuilder.build());
        } catch (JsonProcessingException e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(new HttpUtilsException("Failed to serialize request body", e));
            return future;
        }
    }

    // Helper methods

    /**
     * Creates a URI from a URL string.
     *
     * @param url the URL string
     * @return the URI
     * @throws HttpUtilsException if the URL is invalid
     */
    private URI createUri(String url) throws HttpUtilsException {
        try {
            return URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new HttpUtilsException("Invalid URL: " + url, e);
        }
    }

    /**
     * Adds headers to an HttpRequest.Builder.
     *
     * @param requestBuilder the builder to add headers to
     * @param headers the headers to add
     */
    private void addHeaders(HttpRequest.Builder requestBuilder, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }
    }

    /**
     * Sends an HTTP request synchronously and returns the response body as a string.
     *
     * @param request the request to send
     * @return the response body as a string
     * @throws HttpUtilsException if an error occurs
     */
    private String sendRequest(HttpRequest request) throws HttpUtilsException {
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (isSuccessful(response.statusCode())) {
                return response.body();
            } else {
                throw new HttpUtilsException("HTTP Error: " + response.statusCode() + " - " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new HttpUtilsException("Failed to send request", e);
        }
    }

    /**
     * Sends an HTTP request asynchronously and returns a CompletableFuture with the response body as a string.
     *
     * @param request the request to send
     * @return a CompletableFuture that completes with the response body
     */
    private CompletableFuture<String> sendRequestAsync(HttpRequest request) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (isSuccessful(response.statusCode())) {
                        return response.body();
                    } else {
                        throw new HttpUtilsException("HTTP Error: " + response.statusCode() + " - " + response.body());
                    }
                });
    }

    /**
     * Checks if an HTTP status code indicates success.
     *
     * @param statusCode the status code to check
     * @return true if the status code indicates success, false otherwise
     */
    private boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Deserializes a JSON string to an object of the specified type.
     *
     * @param <T> the type to deserialize to
     * @param json the JSON string to deserialize
     * @param clazz the class of the type to deserialize to
     * @return the deserialized object
     * @throws HttpUtilsException if deserialization fails
     */
    private <T> T deserialize(String json, Class<T> clazz) throws HttpUtilsException {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new HttpUtilsException("Failed to deserialize response", e);
        }
    }

    /**
     * Deserializes a JSON string to an object of the specified type reference.
     *
     * @param <T> the type to deserialize to
     * @param json the JSON string to deserialize
     * @param typeReference the type reference to deserialize to
     * @return the deserialized object
     * @throws HttpUtilsException if deserialization fails
     */
    private <T> T deserialize(String json, TypeReference<T> typeReference) throws HttpUtilsException {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new HttpUtilsException("Failed to deserialize response", e);
        }
    }

}