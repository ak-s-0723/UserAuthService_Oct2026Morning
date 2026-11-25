package org.example.userauthservice_oct2026morning.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserDto {
    private String name;

    private String email;

    private Long id;

    private List<String> roles;
}
