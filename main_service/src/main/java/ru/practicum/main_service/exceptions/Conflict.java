package ru.practicum.main_service.exceptions;

public class Conflict extends RuntimeException {
    public Conflict(String message) {
        super(message);
    }
}
