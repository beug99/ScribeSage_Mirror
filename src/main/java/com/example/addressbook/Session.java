package com.example.addressbook;

/** This class can be used to keep track of the currently-logged-in user
 *  for account-related functions
  */
public class Session {
    private static String loggedInEmail;
    private static String firstName;
    private static String lastName;
    private static Integer userId;

    public static void setUser(String email, String fName, String lName, Integer id) {
        loggedInEmail = email;
        firstName = fName;
        lastName = lName;
        userId = id;
    }

    public static void setLoggedInEmail(String email) {
        Session.loggedInEmail = email;
    }

    public static String getLoggedInEmail() {
        return Session.loggedInEmail;
    }

    public static void setUserId(Integer id) {
        Session.userId = id;
    }

    public static Integer getUserId() {
        return Session.userId;
    }
    
    public static String getFirstName() { return firstName; }

    public static String getLastName() { return lastName; }

    public static void clear(){
        loggedInEmail = null;
        firstName = null;
        lastName = null;
        userId = null;
    }
}

