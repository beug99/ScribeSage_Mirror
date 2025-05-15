import com.example.addressbook.model.MockAIService;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class AI_IntegrationTest {

    @Test
    void testPromptFormatting() {
        MockAIService service = new MockAIService();
        String prompt = service.createPrompt("Enhance my notes");
        assertTrue(prompt.contains("Enhance my notes"));
        assertTrue(prompt.contains("Please enhance"));
    }

    @Test
    void emptyTestPrompt() {
        MockAIService service = new MockAIService();
        String prompt = service.createPrompt("");
        assertFalse(prompt.isEmpty());
    }

}