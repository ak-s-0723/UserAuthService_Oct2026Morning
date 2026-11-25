package org.example.userauthservice_oct2026morning.services;

import org.example.userauthservice_oct2026morning.models.User;

public interface IAuthService {

    User signup(String name, String phoneNumber, String email, String password);

    User login(String email, String password);
}
