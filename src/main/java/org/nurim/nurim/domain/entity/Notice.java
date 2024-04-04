package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @Column(length = 50)
    private String noticeTitle;

    @Column(length = 500)
    private String noticeContent;

    @CreationTimestamp
    private LocalDate noticeRegisterDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "adminId")
    private Admin admin;
}
