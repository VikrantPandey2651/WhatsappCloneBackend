package com.vikrant.whatsappclone.user;


import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserMapper {


    public User fromTokenAttributes(Map<String, Object> attributes) {

        User user = new User();

        if(attributes.containsKey("sub")){
            user.setId(attributes.get("sub").toString());
        }

        if(attributes.containsKey("given_name")){
            user.setFirstName(attributes.get("given_name").toString());
        }else if(attributes.containsKey("nickname")){
            user.setFirstName(attributes.get("nickname").toString());
        }

        if(attributes.containsKey("family_name")){
            user.setLastName(attributes.get("family_name").toString());
        }

        user.setLastSeen(LocalDateTime.now());


        return user;
    }


    public UserResponse toUserResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isOnline(user.isUserOnline())
                .lastSeen(user.getLastSeen())
                .build();


    }
}
