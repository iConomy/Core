package com.iCo6.command.exceptions;

public class InvalidUsage extends Exception {
    public InvalidUsage(String message) {
        super("<rose>Invalid Command Usage: " + message);
    }
}
