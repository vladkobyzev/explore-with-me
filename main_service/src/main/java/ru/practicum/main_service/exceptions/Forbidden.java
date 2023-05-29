package ru.practicum.main_service.exceptions;

public class Forbidden extends RuntimeException {
    public Forbidden(String message) {
        super(message);
    }

}
