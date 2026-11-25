package org.example.userauthservice_oct2026morning.services;

import org.example.userauthservice_oct2026morning.exceptions.EmailAlreadyUsedException;
import org.example.userauthservice_oct2026morning.exceptions.IncorrectPasswordException;
import org.example.userauthservice_oct2026morning.exceptions.UserNotRegisteredException;
import org.example.userauthservice_oct2026morning.models.Role;
import org.example.userauthservice_oct2026morning.models.User;
import org.example.userauthservice_oct2026morning.repos.RoleRepo;
import org.example.userauthservice_oct2026morning.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Override
    public User signup(String name, String phoneNumber, String email, String password) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
           throw new EmailAlreadyUsedException("Please use another email Id");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);  // BCryptPassword Encoder will be used to hash this
        user.setPhoneNumber(phoneNumber);

        Role role = null;
        Optional<Role> optionalRole = roleRepo.findByValue("NON_ADMIN");
        if (optionalRole.isEmpty()) {
            role = new Role();
            role.setValue("NON_ADMIN");
            roleRepo.save(role);
        } else {
            role = optionalRole.get();
        }

        List<Role> roleList = new ArrayList<>();
        roleList.add(role);
        user.setRoleList(roleList);

        return userRepo.save(user);
    }

    @Override
    public User login(String email, String password) {
       Optional<User> userOptional = userRepo.findByEmail(email);
       if (userOptional.isEmpty()) {
           throw new UserNotRegisteredException("Please signup first");
       }

       User user = userOptional.get();
       if (!password.equals(user.getPassword())) {
          throw new IncorrectPasswordException("Please type correct password");
       }

       //Token Generation

       return user;
    }
}
