package com.companyname.project.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.IOException;
import java.io.InputStream;

/**
 * EnvironmentLoader
 * Loads environment configuration from AWS Secrets Manager if configured,
 * otherwise falls back to local JSON files under /src/main/resources/testdata/.
 * 
 * Usage:
 *   -Daws_secret_name=aws_saviynt_testdata -Daws_region=us-east-1 -Denv=dev
 * or set environment variables:
 *   AWS_SECRET_NAME, AWS_REGION, ENV
 */
public class EnvironmentLoader {

    private static JsonNode envConfig;

    public static void load(String env, String fileName) {
        ObjectMapper mapper = new ObjectMapper();

        // Cross-platform safe property + environment variable fallback
        String awsSecretName = System.getProperty("aws_secret_name",
                System.getenv("AWS_SECRET_NAME") != null ? System.getenv("AWS_SECRET_NAME") : "").trim();

        String awsRegion = System.getProperty("aws_region",
                System.getenv("AWS_REGION") != null ? System.getenv("AWS_REGION") : "us-east-1").trim();

        boolean useAws = (awsSecretName != null && !awsSecretName.isEmpty());

        try {
            if (useAws) {
                System.out.println("[INFO] Loading test data from AWS Secrets Manager: " + awsSecretName);
                JsonNode root = fetchAwsSecret(awsSecretName, awsRegion, mapper);

                if (!root.has(env)) {
                    throw new RuntimeException("Environment not found in AWS Secret: " + env);
                }

                envConfig = root.path(env);
                System.out.println("[INFO] Environment loaded successfully from AWS Secret: " + env);

            } else {
                System.out.println("[WARN] AWS Secret not configured. Using local test data file: " + fileName);
                loadFromLocalFile(env, fileName, mapper);
            }

            System.out.println("[DEBUG] Available keys:");
            envConfig.fieldNames().forEachRemaining(System.out::println);

        } catch (IOException | RuntimeException e) {
            System.err.println("[ERROR] AWS Secret load failed: " + e.getMessage());
            System.err.println("[INFO] Falling back to local testdata JSON...");
            try {
                loadFromLocalFile(env, fileName, mapper);
            } catch (IOException ioException) {
                throw new RuntimeException("Failed to load environment configuration: " + ioException.getMessage(), ioException);
            }
        }
    }

    private static JsonNode fetchAwsSecret(String secretName, String regionName, ObjectMapper mapper) throws IOException {
        Region region = Region.of(regionName);
        try (SecretsManagerClient client = SecretsManagerClient.builder().region(region).build()) {
            GetSecretValueRequest request = GetSecretValueRequest.builder()
                    .secretId(secretName)
                    .build();
            GetSecretValueResponse response = client.getSecretValue(request);
            String secretString = response.secretString();

            if (secretString == null || secretString.isEmpty()) {
                throw new RuntimeException("Empty secret value retrieved from AWS Secret: " + secretName);
            }

            return mapper.readTree(secretString);
        }
    }

    private static void loadFromLocalFile(String env, String fileName, ObjectMapper mapper) throws IOException {
        String resourcePath = "testdata/" + fileName; // /src/main/resources/testdata/
        try (InputStream is = EnvironmentLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new RuntimeException("File not found in classpath: " + resourcePath);
            }
            JsonNode root = mapper.readTree(is);
            envConfig = root.path(env);
            if (envConfig.isMissingNode()) {
                throw new RuntimeException("Environment not found in local JSON: " + env);
            }
            System.out.println("[INFO] ✅ Loaded environment from local file: " + env);
        }
    }

    public static String get(String key) {
        if (envConfig == null) {
            throw new RuntimeException("Environment not loaded. Call EnvironmentLoader.load(env, fileName) first.");
        }

        JsonNode currentNode = envConfig;
        for (String part : key.split("\\.")) {
            currentNode = currentNode.path(part);
            if (currentNode.isMissingNode() || currentNode.isNull()) {
                throw new RuntimeException("Key not found in environment configuration: " + key);
            }
        }

        return currentNode.asText();
    }
}