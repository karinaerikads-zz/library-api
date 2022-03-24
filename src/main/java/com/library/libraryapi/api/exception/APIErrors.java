package com.library.libraryapi.api.exception;

import com.library.libraryapi.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.Binding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APIErrors {
    private List<String> errors;

    public APIErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public APIErrors(BusinessException businessException) {
        this.errors = Arrays.asList(businessException.getMessage());
    }

    public APIErrors(ResponseStatusException exceptionResponseStatus){
        this.errors = Arrays.asList(exceptionResponseStatus.getReason());
    }

    public List<String> getErrors() {
        return errors;
    }
}
