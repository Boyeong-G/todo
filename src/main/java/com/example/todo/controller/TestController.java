package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.TestRequestBodyDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    // localhost:8080/test
    @GetMapping
    public String testController() {
        return "Hello World!";
    }

    // localhost:8080/test/testGetMapping
    @GetMapping("/testGetMapping")
    public String testControllerWithPath() {
        return "Hello World! testGetMapping";
    }

    // localhost:8080/test/(id값)
    @GetMapping("/{id}")
    public String testControllerWithPathVariables(@PathVariable(required = false) int id) {
        return "Hello World! ID" + id;
    }

    // localhost:8080/test/testRequestParam?id=(id값)
    @GetMapping("/testRequestParam")
    public String testControllerRequestParam(@RequestParam(required = false) int id) {
        return "Hello World! ID" + id;
    }

    // localhost:8080/test/testRequestBody
    @GetMapping("/testRequestBody")
    public ResponseDto<String> testControllerRequestBody() {
        List<String> list = new ArrayList<>();
        list.add("Hello World! I'm ResponseDto");
        ResponseDto<String> response = ResponseDto.<String>builder().data(list).build();
        return response;
    }

    // ResponseDto를 리턴하는 것과 ResponseEntity를 리턴하는 것은 body에서 아무 차이가 없지만
    // ResponseEntity를 사용하면 status와 header를 조작할 수 있음
    // localhost:8080/test/testResponseEntity
    @GetMapping("/testResponseEntity")
    public ResponseEntity<?> testControllerResponseEntity() {
        List<String> list = new ArrayList<>();
        list.add("Hello World! I'm ResponseEntity. And you get 400!");
        ResponseDto<String> response = ResponseDto.<String>builder().data(list).build();

        // http status를 400으로 설정.
        // badRequest 메서드 대신 ok 메서드를 사용하면 정상적 응답을 반환
        return ResponseEntity.badRequest().body(response);
    }
}
