package com.iCo6.IO.exceptions;

public class MissingDriver extends Exception {
    public MissingDriver(String file) {
        super("Missing Driver: " + file);
    }
}
