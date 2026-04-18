package com.example.library.exception;

public record ValidationError(String field, String message) {
}