package com.example.addressbook.model;

public class MockAIService {

    public String createPrompt(String prompt){
        return "Please enhance the following notes:\n" + prompt;
    }
}
