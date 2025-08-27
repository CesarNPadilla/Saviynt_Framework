package com.companyname.project.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class EnvironmentLoader {

    private static JsonNode envConfig;

    public static void load(String env, String fileName) {
        ObjectMapper mapper = new ObjectMapper();
        String resourcePath = "testdata/" + fileName; // point to resources/testdata/
        try (InputStream is = EnvironmentLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("Failed to find " + resourcePath + " in classpath");
            }
            JsonNode root = mapper.readTree(is);
            envConfig = root.path(env);
            if (envConfig.isMissingNode()) {
                throw new RuntimeException("Environment not found in config: " + env);
            }

            System.out.println("Loaded environment: " + env);
            System.out.println("Available keys: ");
            envConfig.fieldNames().forEachRemaining(System.out::println);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + resourcePath, e);
        }
    }

    public static String get(String key) {
        if (envConfig == null) {
            throw new RuntimeException("Environment not loaded. Call EnvironmentLoader.load(env, fileName) first.");
        }

        JsonNode currentNode = envConfig;
        for (String part : key.split("\\.")) { // traverse nested keys
            currentNode = currentNode.path(part);
            if (currentNode.isMissingNode() || currentNode.isNull()) {
                throw new RuntimeException("Key not found in JSON: " + key);
            }
        }

        return currentNode.asText();
    }

}