package org.example.userauthservice_oct2026morning.controllers;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice_oct2026morning.dtos.LoginRequestDto;
import org.example.userauthservice_oct2026morning.dtos.SignupRequestDto;
import org.example.userauthservice_oct2026morning.dtos.UserDto;
import org.example.userauthservice_oct2026morning.dtos.ValidateTokenRequestDto;
import org.example.userauthservice_oct2026morning.models.Role;
import org.example.userauthservice_oct2026morning.models.User;
import org.example.userauthservice_oct2026morning.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    //login --
    //signup --
    //logout
    //forgetPassword

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignupRequestDto signupRequestDto) {
        User user = authService.signup(signupRequestDto.getName(),
                signupRequestDto.getPhoneNumber(),
                signupRequestDto.getEmail(),
                signupRequestDto.getPassword());

        return from(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
       Pair<User,String> response =
               authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

       String token = response.b;
        MultiValueMap<String,String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.SET_COOKIE,"auth_session_id ="+token);

       return new ResponseEntity<>(from(response.a), headers, HttpStatus.OK);
    }

    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenRequestDto validateTokenRequestDto) {
        return authService.validateToken(validateTokenRequestDto.getToken());
    }

    private UserDto from(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setId(user.getId());
        List<String> roles = new ArrayList<>();
        for(Role role : user.getRoleList()) {
            roles.add(role.getValue());
        }
        userDto.setRoles(roles);
        return userDto;
    }
}
