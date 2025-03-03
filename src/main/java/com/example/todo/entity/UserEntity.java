package com.example.todo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints={@UniqueConstraint(columnNames="username")})
public class UserEntity {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid")
    private String id;

    // 유저 아이디로 사용할 유저 네임 (이메일 또는 문자열)
    @Column(nullable=false)
    private String username;

    // 패스워드
    private String password;

    // 사용자의 롤 (예. 어드민. 일반 사용자)
    private String role;

    // 이후 OAuth에서 사용할 유저정보 제공자 (github)
    private String authProvider;
}
