package com.example.todo.controller;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.TodoDto;
import com.example.todo.entity.TodoEntity;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todo")
public class TodoController {
    @Autowired
    private TodoService service;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo() {
        String str = service.testService();
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDto<String> response = ResponseDto.<String>builder().data(list).build();
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<?> retrieveTodoList() {
        String temporaryUserId = "temporary-user";

        // (1) 서비스 메서드의 retrieve 메서드를 사용해 to-do 리스트를 가져옴
        List<TodoEntity> entities = service.retrieve(temporaryUserId);

        // (2) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDto 리스트로 변환
        List<TodoDto> dtos = entities.stream().map(TodoDto::new).collect(Collectors.toList());

        // (6) 변환된 TodoDto 리스트를 ResponseDto로 초기화
        ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().data(dtos).build();

        // (7) ResponseDto를 리턴
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@RequestBody TodoDto dto) {
        try {
            String temporaryUserId = "temporary-user";

            // (1) TodoEntity로 변환
            TodoEntity entity = TodoDto.toEntity(dto);

            // (2) id를 null로 초기화 (생성 당시에는 id가 없어야 하기 때문)
            entity.setId(null);

            // (3) 임시 유저 아이디를 설정 (지금은 인증과 인가 기능이 없으므로 한 유저(temporary-user)만 로그인 없이 사용 가능한 어플리케이션)
            entity.setUserId(temporaryUserId);

            // (4) 서비스를 이용해 TodoEntity를 생성
            List<TodoEntity> entities = service.create(entity);

            // (5) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDto 리스트로 변환
            List<TodoDto> dtos = entities.stream().map(TodoDto::new).collect(Collectors.toList());

            // (6) 변환된 TodoDto 리스트를 이용해 ResponseDto를 초기화
            ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().data(dtos).build();

            // (7) ResponseDto를 리턴
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            // (8) 혹시 예외가 나는 경우 dto 대신 error에 메시지를 넣어 리턴
            String error = e.getMessage();
            ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@RequestBody TodoDto dto) {
        String temporaryUserId = "temporary-user"; // temporary user id.

        // (1) dto를 entity로 변환
        TodoEntity entity = TodoDto.toEntity(dto);

        // (2) id를 temporaryUserId로 초기화
        entity.setUserId(temporaryUserId);

        // (3) 서비스를 이용해 entity를 업데이트
        List<TodoEntity> entities = service.update(entity);

        // (4) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDto 리스트로 변환
        List<TodoDto> dtos = entities.stream().map(TodoDto::new).collect(Collectors.toList());

        // (5) 변환된 TodoDto 리스트를 이용해 ResponseDto를 초기화
        ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().data(dtos).build();

        // (6) ResponseDto를 리턴
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@RequestBody TodoDto dto) {
        try {
            String temporaryUserId = "temporary-user";

            // (1) TodoEntity로 변환
            TodoEntity entity = TodoDto.toEntity(dto);

            // (2) 임시 유저 아이디를 설정
            entity.setUserId(temporaryUserId);

            // (3) 서비스를 이용해 entity를 삭제
            List<TodoEntity> entities = service.delete(entity);

            // (4) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDto 리스트로 변환
            List<TodoDto> dtos = entities.stream().map(TodoDto::new).collect(Collectors.toList());

            // (5) 변환된 TodoDto 리스트를 이용해 ResponseDto를 초기화
            ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().data(dtos).build();

            // (6) ResponseDto를 리턴
            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            // (8) 혹시 예외가 나는 경우 dto 대신 error에 메시지를 넣어 리턴한다.
            String error = e.getMessage();
            ResponseDto<TodoDto> response = ResponseDto.<TodoDto>builder().error(error).build();

            return ResponseEntity.badRequest().body(response);
        }
    }
}
