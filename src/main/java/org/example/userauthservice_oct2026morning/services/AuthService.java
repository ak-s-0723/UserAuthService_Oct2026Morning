package org.example.userauthservice_oct2026morning.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthservice_oct2026morning.exceptions.EmailAlreadyUsedException;
import org.example.userauthservice_oct2026morning.exceptions.IncorrectPasswordException;
import org.example.userauthservice_oct2026morning.exceptions.UserNotRegisteredException;
import org.example.userauthservice_oct2026morning.models.Role;
import org.example.userauthservice_oct2026morning.models.Status;
import org.example.userauthservice_oct2026morning.models.User;
import org.example.userauthservice_oct2026morning.models.UserSession;
import org.example.userauthservice_oct2026morning.repos.RoleRepo;
import org.example.userauthservice_oct2026morning.repos.UserRepo;
import org.example.userauthservice_oct2026morning.repos.UserSessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserSessionRepo userSessionRepo;

    @Autowired
    private SecretKey secretKey;

    @Override
    public User signup(String name, String phoneNumber, String email, String password) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
           throw new EmailAlreadyUsedException("Please use another email Id");
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));  // BCryptPassword Encoder will be used to hash this
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
    public Pair<User,String> login(String email, String password) {
       Optional<User> userOptional = userRepo.findByEmail(email);
       if (userOptional.isEmpty()) {
           throw new UserNotRegisteredException("Please signup first");
       }

       User user = userOptional.get();
       if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
       //if (!password.equals(user.getPassword())) {
          throw new IncorrectPasswordException("Please type correct password");
       }

       //Token Generation
        Map<String,Object> claims = new HashMap<>();
        claims.put("user_id",user.getId());
        claims.put("generated_by","scaler");
        Long currentTime = System.currentTimeMillis();
        claims.put("iat",currentTime);
        claims.put("exp",currentTime+100000);
        List<String> roles = new ArrayList<>();
        for (Role role : user.getRoleList()) {
            roles.add(role.getValue());
        }
        claims.put("access",roles);

//        MacAlgorithm macAlgorithm = Jwts.SIG.HS256;
//        SecretKey secretKey = macAlgorithm.key().build();
        String token = Jwts.builder().claims(claims).signWith(secretKey).compact();

        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setToken(token);
        userSession.setExpiry(currentTime+100000);
        userSessionRepo.save(userSession);

       return new Pair<>(user,token);
    }

    //Alternate approach of validation - https://github.com/ak-s-0723/UserAuthService_Aug2026/commit/7ad4048ac9b1b9a5aec52dea511de28b436bf184#diff-77b3c90b63190c543c8583282e1a6035675999184d440dd66c6f79317ca38764
    public Boolean validateToken(String token) {
        Optional<UserSession> optionalUserSession = userSessionRepo.findByToken(token);
        if (optionalUserSession.isEmpty()) {
            return false;
        }

        UserSession userSession = optionalUserSession.get();
        if (userSession.getStatus().equals(Status.INACTIVE)) {
            return false;
        }

        System.out.println("current Time = "+System.currentTimeMillis());
        System.out.println("expiry = "+userSession.getExpiry());

        if (userSession.getExpiry() < System.currentTimeMillis()) {
            //Token has expired
            userSession.setStatus(Status.INACTIVE);
            userSessionRepo.save(userSession);
            return  false;
        }

        return true;
    }
}
