package com.matpires.login_cookie.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuário não encontrado");
    }
}
