package com.example.addressbook.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;

public class AIService {

    // uses the config.properties file to read the api key, rather than hardcoding it here for security
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";

    private final String apiKey;
    private final HttpClient client;

    // makes connection to AI
    public AIService() {
        this.apiKey = loadApiKey();
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    // loads the api key from the config file
    private String loadApiKey() {
        // First try to load from environment variable (preferred)
        String key = System.getenv("OPENAI_API_KEY");
        if (key != null && !key.isEmpty()) {
            return key;
        }
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("openai.api.key");
        } catch (IOException e) {
            System.err.println("Warning: Could not load API key from properties file: " + e.getMessage());
            return null;
        }
    }

    // uses openAI to enhance text
    public String enhanceText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Please provide text to enhance.";
        }

        try {
            ObjectMapper mapper = new ObjectMapper();

            // messages array, setting AI role
            ArrayNode messages = mapper.createArrayNode();
            messages.add(mapper.createObjectNode()
                    .put("role", "system")
                    .put("content", "You are a helpful assistant that enhances writing. Improve clarity, style, and readability while maintaining the original meaning."));
            messages.add(mapper.createObjectNode()
                    .put("role", "user")
                    .put("content", "Enhance this text for clarity and style: '" + input + "'"));

            // AI receives json body text as input so need to parse string to json
            ObjectNode jsonBody = mapper.createObjectNode();
            jsonBody.put("model", DEFAULT_MODEL);
            jsonBody.set("messages", messages);

            String requestBody = mapper.writeValueAsString(jsonBody);

            // makes http request to API url (provided by openAI, found on openai website)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    // timeout in case it doesn't connect properly
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            System.out.println("sending request to OpenAI");
            // saves response from http (if there's an issue (there were lots) and prints it)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parseCompletionFromResponse(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            return "sorry, I couldn't enhance the text due to a technical issue: " + e.getMessage();
        }
    }

    // returns the response and parses from json to string
    private String parseCompletionFromResponse(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            // check if there's an error message
            if (root.has("error")) {
                String errorMessage = root.path("error").path("message").asText("Unknown error");
                System.err.println("OpenAI API returned an error: " + errorMessage);
                return "AI enhancement failed: " + errorMessage;
            }

            // extract the content
            JsonNode contentNode = root.at("/choices/0/message/content");
            if (contentNode.isMissingNode()) {
                System.err.println("Unexpected response format: " + json);
                return "Sorry, I received an unexpected response format from the AI service.";
            }

            String enhancedText = contentNode.asText();
            // remove surrounding quotes that might have been added
            if (enhancedText.startsWith("'") && enhancedText.endsWith("'")) {
                enhancedText = enhancedText.substring(1, enhancedText.length() - 1);
            }
            return enhancedText;

        } catch (Exception e) {
            e.printStackTrace();
            return "failed to process AI response: " + e.getMessage();
        }
    }
}