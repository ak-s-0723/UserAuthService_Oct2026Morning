package org.example.userauthservice_oct2026morning.services;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice_oct2026morning.models.User;

public interface IAuthService {

    User signup(String name, String phoneNumber, String email, String password);

    Pair<User,String> login(String email, String password);

    Boolean validateToken(String token);
}
