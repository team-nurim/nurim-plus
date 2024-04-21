package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "post")
public class PostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postImageId", nullable = false)
    private Long postImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @Column(name = "image_detail", length = 500)
    private String image_detail;

    @Column(name = "image_thumb", nullable = false, length = 500)
    private String image_thumb;

    public void changePost(Post post) {
        this.post = post;
    }


}