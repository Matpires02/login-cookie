package com.matpires.login_cookie.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciais inválidas");
    }
}
