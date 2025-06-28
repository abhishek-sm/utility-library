package com.github.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import lombok.experimental.UtilityClass;

/**
 * Utility class for JSON operations using Jackson library.
 */
@UtilityClass
@Getter
public class JsonUtils {
    
    /**
     *  Get the ObjectMapper instance for custom configurations.
     *
     * @return The ObjectMapper instance
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Configure the ObjectMapper
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    /**
     * Convert an object to a JSON string.
     *
     * @param object The object to convert
     * @return The JSON string representation
     * @throws JsonProcessingException If conversion fails
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    /**
     * Convert an object to a pretty-printed JSON string.
     *
     * @param object The object to convert
     * @return The pretty-printed JSON string
     * @throws JsonProcessingException If conversion fails
     */
    public static String toPrettyJson(Object object) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /**
     * Convert a JSON string to an object.
     *
     * @param json  The JSON string
     * @param clazz The class of the object
     * @param <T>   The type of the object
     * @return The converted object
     * @throws JsonProcessingException If conversion fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * Convert a JSON string to a List of objects.
     *
     * @param json  The JSON string
     * @param clazz The class of the objects in the list
     * @param <T>   The type of the objects
     * @return The list of converted objects
     * @throws JsonProcessingException If conversion fails
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * Convert a JSON string to a Map with String keys.
     *
     * @param json     The JSON string
     * @param valueType The class of the values in the map
     * @param <T>      The type of the values
     * @return The map of converted values
     * @throws JsonProcessingException If conversion fails
     */
    public static <T> Map<String, T> fromJsonToMap(String json, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructMapType(Map.class, String.class, valueType));
    }

    /**
     * Convert a JSON string to a complex type using TypeReference.
     *
     * @param json          The JSON string
     * @param typeReference The TypeReference for the complex type
     * @param <T>           The type to convert to
     * @return The converted object
     * @throws JsonProcessingException If conversion fails
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(json, typeReference);
    }

    /**
     * Parse a JSON string to a JsonNode.
     *
     * @param json The JSON string
     * @return The parsed JsonNode
     * @throws JsonProcessingException If parsing fails
     */
    public static JsonNode parseJson(String json) throws JsonProcessingException {
        return objectMapper.readTree(json);
    }

    /**
     * Read a JSON file into an object.
     *
     * @param file  The file to read
     * @param clazz The class of the object
     * @param <T>   The type of the object
     * @return The read object
     * @throws IOException If reading fails
     */
    public static <T> T readJsonFile(File file, Class<T> clazz) throws IOException {
        return objectMapper.readValue(file, clazz);
    }

    /**
     * Read a JSON file into a JsonNode.
     *
     * @param file The file to read
     * @return The read JsonNode
     * @throws IOException If reading fails
     */
    public static JsonNode readJsonFileAsNode(File file) throws IOException {
        return objectMapper.readTree(file);
    }

    /**
     * Read JSON from an InputStream into an object.
     *
     * @param inputStream The InputStream to read from
     * @param clazz       The class of the object
     * @param <T>         The type of the object
     * @return The read object
     * @throws IOException If reading fails
     */
    public static <T> T readJsonFromStream(InputStream inputStream, Class<T> clazz) throws IOException {
        return objectMapper.readValue(inputStream, clazz);
    }

    /**
     * Write an object to a JSON file.
     *
     * @param file   The file to write to
     * @param object The object to write
     * @throws IOException If writing fails
     */
    public static void writeJsonFile(File file, Object object) throws IOException {
        objectMapper.writeValue(file, object);
    }

    /**
     * Write an object to a pretty-printed JSON file.
     *
     * @param file   The file to write to
     * @param object The object to write
     * @throws IOException If writing fails
     */
    public static void writePrettyJsonFile(File file, Object object) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
    }

    /**
     * Create an empty ObjectNode (JSON object).
     *
     * @return A new empty ObjectNode
     */
    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    /**
     * Create an empty ArrayNode (JSON array).
     *
     * @return A new empty ArrayNode
     */
    public static ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

    /**
     * Extract a value from a JsonNode safely.
     *
     * @param node    The JsonNode to extract from
     * @param field   The field name to extract
     * @param clazz   The class of the value
     * @param <T>     The type of the value
     * @return An Optional containing the value, or empty if extraction failed
     */
    public static <T> Optional<T> extractValue(JsonNode node, String field, Class<T> clazz) {
        if (node == null || !node.has(field)) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(objectMapper.treeToValue(node.get(field), clazz));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    /**
     * Check if a string is valid JSON.
     *
     * @param json The string to check
     * @return true if the string is valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Merge two JSON objects (represented as strings).
     *
     * @param json1 The first JSON object string
     * @param json2 The second JSON object string
     * @return The merged JSON object string
     * @throws JsonProcessingException If merging fails
     */
    public static String mergeJsonObjects(String json1, String json2) throws JsonProcessingException {
        JsonNode node1 = objectMapper.readTree(json1);
        JsonNode node2 = objectMapper.readTree(json2);

        if (!node1.isObject() || !node2.isObject()) {
            throw new IllegalArgumentException("Both JSON strings must represent JSON objects");
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.setAll((ObjectNode) node1);
        result.setAll((ObjectNode) node2);

        return objectMapper.writeValueAsString(result);
    }

}
