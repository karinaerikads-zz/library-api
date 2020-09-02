package com.library.libraryapi.api.exception;

import org.springframework.validation.BindingResult;

import javax.naming.Binding;
import java.util.ArrayList;
import java.util.List;

public class APIErrors {
    private List<String> errors;

    public APIErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }
}
