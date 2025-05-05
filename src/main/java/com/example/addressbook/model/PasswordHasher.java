package com.example.addressbook.model;

import com.password4j.Password;
import com.password4j.BadParametersException;

public class PasswordHasher {

    public static String hashPassword(String plainTextPassword){
        try {
        // using argon2id
            return Password.hash(plainTextPassword).withArgon2().getResult();
        }   catch (BadParametersException e){
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String plainTextPassword, String passwordHash){
        try {
            return Password.check(plainTextPassword, passwordHash).withArgon2();
        } catch (Exception e){
            System.err.println("Password verification error: " + e.getMessage());
            // return false for security
            return false;
        }
    }
}
