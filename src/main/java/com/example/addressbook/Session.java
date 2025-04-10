package com.example.addressbook;

/** This class can be used to keep track of the currently-logged-in user
 *  for account-related functions
  */
public class Session {
    private static String loggedInEmail;

    public static void setLoggedInEmail(String email) {
        Session.loggedInEmail = email;
    }

    public static String getLoggedInEmail() {
        return Session.loggedInEmail;
    }

    public static void clear(){
        loggedInEmail = null;
    }
}

