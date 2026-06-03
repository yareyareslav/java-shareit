package ru.practicum.shareit.shared.error;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerUnitTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    @DisplayName("Handle unexpected exception")
    void handleUnexpected_return500Message() {
        ErrorResponse response = errorHandler.handleUnexpected(new RuntimeException("boom"));

        assertEquals("Внутренняя ошибка сервера", response.error());
    }

    @Test
    @DisplayName("Handle constraint violation")
    void handleConstraintViolation_returnFirstViolationMessage() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("email");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        ErrorResponse response = errorHandler.handleConstraintViolation(
                new ConstraintViolationException(Set.of(violation)));

        assertEquals("email: must not be blank", response.error());
    }

    @Test
    @DisplayName("Handle validation without field errors")
    void handleValidation_withoutFieldErrors_returnDefaultMessage() throws NoSuchMethodException {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult);

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertEquals("Ошибка валидации", response.error());
    }

    @Test
    @DisplayName("Handle validation with field error")
    void handleValidation_withFieldError_returnFieldMessage() throws NoSuchMethodException {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "name", "must not be blank"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                null, bindingResult);

        ErrorResponse response = errorHandler.handleValidation(exception);

        assertEquals("name: must not be blank", response.error());
    }
}
