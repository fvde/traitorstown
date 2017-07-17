package com.individual.thinking.traitorstown.user;

public class RegistrationRequestVo {

    private final String email;
    private final String password;

    public RegistrationRequestVo(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
