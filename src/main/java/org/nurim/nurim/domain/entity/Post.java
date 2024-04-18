package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // 여러 개의 이미지를 한 번에 추가할 수 있도록 변경
    public void addPostImages(List<PostImage> postImages) {
        this.imageSet.addAll(postImages);
        for (PostImage postImage : postImages) {
            postImage.setPost(this);
        }
    }

//    public void addPostImage(String uuid, String fileName) {
//
//        PostImage postImage = PostImage.builder()
//                .image_detail(uuid)  // 파일 uuid 저장
//                .image_thumb(fileName)   // 파일 이름 저장
//                .build();
//
//        this.imageSet.add(postImage);
//        postImage.changePost(this);
//
//    }

    public void update(String postTitle, String postContent, String postWriter, String postCategory, LocalDate postRegisterDate) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postWriter = postWriter;
        this.postCategory = postCategory;
        this.postRegisterDate = postRegisterDate;
    }

}