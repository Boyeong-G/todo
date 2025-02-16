package com.example.todo.dto;

import com.example.todo.entity.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class TodoDto {
    // 아이디
    private String id;

    // to-do (할 일)
    private String title;

    // to-do 완료 확인
    private boolean done;

    public TodoDto(TodoEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.done = entity.isDone();
    }

    public static TodoEntity toEntity(final TodoDto dto) {
        return TodoEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .done(dto.done)
                .build();
    }
}
