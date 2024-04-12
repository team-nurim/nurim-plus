package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminId")
    private Long adminId;

    @Column(updatable = false, nullable = false, unique = true)
    private String adminEmail;

    @Column(nullable = false)
    private String adminPw;

    @Column(updatable = false, nullable = false, unique = true)
    private String adminNickname;

    @OneToMany(mappedBy = "admin", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "admin", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();
}