package com.example.addressbook.model;

import java.sql.Time;
import java.util.concurrent.TimeoutException;

public class MockAIService {

    public String createPrompt(String prompt){
        return "Please enhance the following notes:\n" + prompt;
    }

    public String getResponse (String prompt) throws TimeoutException {
        MockAPICall();
        return "Enhanced: " + prompt;
    }

    private void MockAPICall() throws TimeoutException {
        try {
            Thread.sleep(3000);
        }   catch (InterruptedException e) {
            throw new TimeoutException("AI service timed out, please try agaian");
        }
    }
}
