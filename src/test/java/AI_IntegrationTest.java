import com.example.addressbook.service.MockAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.MockedStatic;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class AI_IntegrationTest {
    private MockAIService service;

    @BeforeEach
    void setUp() {
        service = new MockAIService();
    }

    @Test
    void testPromptFormatting() {
        String prompt = service.createPrompt("Enhance my notes");
        assertTrue(prompt.contains("Enhance my notes"));
        assertTrue(prompt.contains("Please enhance"));
    }

    @Test
    void emptyTestPrompt() {
        String prompt = service.createPrompt("");
        assertFalse(prompt.isEmpty());
    }

    @Test
    void testPromptStructure() {
        String userInput = "Test input";
        String prompt = service.createPrompt(userInput);

        assertEquals("Please enhance the following notes:\n" + userInput, prompt);
        assertTrue(prompt.startsWith("Please enhance the following notes:"));
        assertTrue(prompt.endsWith(userInput));
    }

    @Test
    void testPromptWithSpecialCharacters() {
        String specialInput = "Notes with @#$%^&*()_+ symbols and\nnewlines\ttabs";
        String prompt = service.createPrompt(specialInput);

        assertTrue(prompt.contains(specialInput));
        assertTrue(prompt.contains("@#$%^&*()_+"));
        assertTrue(prompt.contains("\n"));
        assertTrue(prompt.contains("\t"));
    }

    @Test
    void testPromptWithNullInput() {
        String prompt = service.createPrompt(null);
        assertEquals("Please enhance the following notes:\nnull", prompt);
    }

    @Test
    void testGetResponseSuccess() throws TimeoutException {
        String testPrompt = "Test prompt";
        String response = service.getResponse(testPrompt);

        assertNotNull(response);
        assertEquals("Enhanced: " + testPrompt, response);
        assertTrue(response.startsWith("Enhanced: "));
    }

    @Test
    void testGetResponseWithEmptyPrompt() throws TimeoutException {
        String response = service.getResponse("");
        assertEquals("Enhanced: ", response);
    }

    @Test
    void testGetResponseWithNullPrompt() throws TimeoutException {
        String response = service.getResponse(null);
        assertEquals("Enhanced: null", response);
    }

    @Test
    @Timeout(5) // Test should complete within 5 seconds
    void testGetResponseTiming() throws TimeoutException {
        long startTime = System.currentTimeMillis();
        service.getResponse("Test timing");
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        assertTrue(duration >= 3000, "Response should take at least 3 seconds");
        assertTrue(duration < 4000, "Response should not take more than 4 seconds");
    }


}