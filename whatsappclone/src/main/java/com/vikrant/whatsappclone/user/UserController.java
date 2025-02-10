package com.vikrant.whatsappclone.user;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "user")
public class UserController {


    private final UserService  userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUser(Authentication authentication){
        return ResponseEntity.ok(userService.getAllUsersExceptSelf(authentication));
    }

}
