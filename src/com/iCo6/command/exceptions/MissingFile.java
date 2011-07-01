package com.iCo6.command.exceptions;

public class MissingFile extends Exception {
    public MissingFile(String file) {
        super("File could not be found: " + file);
    }
}
