import java.util.concurrent.TimeoutException;

/**
 * Mock implementation of an AI service for testing
 */
public class MockAIService {

    /**
     * Creates a formatted prompt for the AI service
     * @param input The user input to format
     * @return A formatted prompt
     */
    public String createPrompt(String input) {
        if (input.isEmpty()) {
            return "Please provide some text to enhance. Default prompt.";
        }
        return "Please enhance the following text: " + input;
    }

    /**
     * Simulates getting a response from an AI service
     * @param prompt The prompt to send to the AI service
     * @return The AI response
     * @throws TimeoutException if the service times out
     */
    public String getResponse(String prompt) throws TimeoutException {
        // For testing the timeout scenario
        if (prompt.contains("FORCE_TIMEOUT")) {
            throw new TimeoutException("AI service timed out");
        }

        // Simulate a response
        return "AI enhanced version of: " + prompt;
    }
}