package com.example.todo.service;

import com.example.todo.entity.TodoEntity;
import com.example.todo.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j // 로깅
@Service
public class TodoService {
    @Autowired
    private TodoRepository repository;

    public String testService() {
        TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
        repository.save(entity);
        TodoEntity savedEntity = repository.findById(entity.getId()).get();
        return savedEntity.getTitle();
    }

    private void validate(final TodoEntity entity) {
        if (entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if (entity.getUserId() == null) {
            log.warn("Unknown User");
            throw new RuntimeException("Unknown User");
        }
    }

    public List<TodoEntity> retrieve(final String userId) {
        return repository.findByUserId(userId);
    }

    public List<TodoEntity> create(final TodoEntity entity) {
        // validations
        validate(entity);

        repository.save(entity);

        log.info("Entity ID : {} is saved.", entity.getId());

        return repository.findByUserId(entity.getUserId());
    }

    public List<TodoEntity> update(final TodoEntity entity) {
        // (1) 저장 할 엔티티가 유효한지 확인
        validate(entity);

        // (2) 넘겨받은 엔티티 id를 이용해 TodoEntity를 가져옴 (존재하지 않는 엔티티는 업데이트 할 수 없기 때문)
        final Optional<TodoEntity> original = repository.findById(entity.getId());

        // lambda 사용
        original.ifPresent(todo -> {
            // (3) 반환된 TodoEntity가 존재하면 값을 새 entity의 값으로 덮어 씌운
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());

            // (4) 데이터베이스에 새 값을 저장
            repository.save(todo);
        });

        // retrieve 메서드를 이용해 유저의 모든 to-do 리스트를 리턴
        return retrieve(entity.getUserId());
    }

    public List<TodoEntity> delete(final TodoEntity entity) {
        // (1) 저장 할 엔티티가 유효한지 확인
        validate(entity);

        try {
            // (2) 엔티티를 삭제
            repository.delete(entity);
        } catch(Exception e) {
            // (3) exception 발생시 id와 exception을 로깅
            log.error("error deleting entity ", entity.getId(), e);

            // (4) 컨트롤러로 exception을 날림
            // (데이터베이스 내부 로직을 캡슐화 하기 위해 e를 리턴하지 않고 새 exception 오브젝트를 리턴)
            throw new RuntimeException("error deleting entity " + entity.getId());
        }
        // (5) 새 to-do 리스트를 가져와 리턴
        return retrieve(entity.getUserId());
    }
}
