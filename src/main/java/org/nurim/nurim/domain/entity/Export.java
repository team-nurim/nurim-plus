package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Export {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exportId;

    @Column(length = 30, nullable = false, unique = true)
    private String exportEmail;

    @Column(length = 100,nullable = false)
    private String exportPw;

    @Column(length = 25,nullable = false)
    private String exportNickname;

    @Column(length = 100)
    private int age;

    @Column(nullable = false)
    private boolean exportGender;

    @Column(nullable = false)
    private String exportResidence;

    @Column(nullable = false)
    private String exportIncome;

    @Column(nullable = false)
    private String exportCertificate;

    @OneToMany(mappedBy = "export", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> community = new ArrayList<>();
}
