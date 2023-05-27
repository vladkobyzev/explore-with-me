package ru.practicum.main_service.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {
    private int status;
    private String reason;
    private String message;
    private String timestamp;
}
