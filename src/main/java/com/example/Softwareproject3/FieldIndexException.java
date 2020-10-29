package com.example.Softwareproject3;

/**
 * The given field index is out of bounds if this exception is thrown.
 */
public class FieldIndexException extends Exception {
    public FieldIndexException(String message) {
        super("Field index is out of bounds.");
    }

}
