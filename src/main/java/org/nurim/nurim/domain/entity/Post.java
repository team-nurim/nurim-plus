package org.nurim.nurim.domain.entity;


import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Long postId;

    @Column(nullable = false)
    private String postWriter;

    @Column(name = "post_title", nullable = false)
    private String postTitle;

    @Column(name = "post_content", nullable = false)
    private String postContent;

    @Column(name = "post_category", nullable = false)
    private String postCategory;

    @Column(name = "post_register_date", nullable = false)
    private LocalDate postRegisterDate;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, orphanRemoval = true) // 어떤 Entity의 속성으로 매핑하는지 // CurationImage의 curation// 변수
    @Builder.Default
    private List<PostImage> imageSet = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

    public void update(String postTitle, String postContent, String postWriter, String postCategory, LocalDate postRegisterDate) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postWriter = postWriter;
        this.postCategory = postCategory;
        this.postRegisterDate = postRegisterDate;
    }

}