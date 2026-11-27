package org.example.userauthservice_oct2026morning.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class UserSession extends BaseModel {
    private String token;

    private Long expiry;

    @ManyToOne
    private User user;

}

//1           M
//User       Session
//1            1
//
//1 : M
