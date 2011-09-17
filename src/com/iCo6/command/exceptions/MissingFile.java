package com.iCo6.command.exceptions;

public class MissingFile extends Exception {
    public MissingFile(String file) {
        super("<rose>File could not be found: <white>" + file);
    }
}
