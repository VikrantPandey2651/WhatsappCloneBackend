package com.vikrant.whatsappclone.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.util.Map;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    //getting email from Auth token
    //checking user with email is present in DB
    //mapping user from token
    //saving the user to DB with updated data
    public void synchronizeWithIdp(Jwt token) {
        log.info("Synchronizing user with idp");
        getUserEmail(token).ifPresent(userEmail -> {
            log.info("Synchronizing user having email {}", userEmail);
            Optional<User> optUser = userRepository.findByEmail(userEmail);
            User user = userMapper.fromTokenAttributes(token.getClaims());
            optUser.ifPresent(user1 -> user.setId(optUser.get().getId()));

            userRepository.save(user);
        });

    }




    private Optional<String> getUserEmail(Jwt token){
        Map<String, Object> attributes = token.getClaims();
        if(attributes.containsKey("email")){
            return Optional.of(attributes.get("email").toString());
        }
        return Optional.empty();


    }
}
