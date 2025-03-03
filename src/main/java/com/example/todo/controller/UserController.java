package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.UserDto;
import com.example.todo.entity.UserEntity;
import com.example.todo.security.TokenProvider;
import com.example.todo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private TokenProvider tokenProvider;

    // Bean으로 작성 가능
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        try {
            if (userDto == null || userDto.getPassword() == null) {
                throw new RuntimeException("Invalid Password value.");
            }

            // 요청을 이용해 저장할 유저 만들기
            UserEntity user = UserEntity.builder()
                    .username(userDto.getUsername())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .build();

            // 서비스를 이용해 리포지터리에 유저 저장
            UserEntity registerdedUser = userService.create(user);
            UserDto responseUserDto = UserDto.builder()
                    .id(registerdedUser.getId())
                    .username(registerdedUser.getUsername())
                    .build();

            return ResponseEntity.ok().body(responseUserDto);
        } catch (Exception e) {
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();

            return ResponseEntity.badRequest().body(responseDto);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDto userDto) {
        UserEntity user = userService.getByCredentials(
                userDto.getUsername(),
                userDto.getPassword(),
                passwordEncoder
        );

        if (user != null) {
            // 토큰 생성
            final String token = tokenProvider.create(user);
            final UserDto responseUserDto = UserDto.builder()
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDto);
        } else {
            ResponseDto responseDto = ResponseDto.builder()
                    .error("Login failed.")
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDto);
        }
    }

}