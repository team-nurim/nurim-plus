package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityId;

    @Column(length = 50)
    private String title;

    @Column(length = 500)
    private String content;

    @CreationTimestamp
    private LocalDateTime registerDate;

    @UpdateTimestamp
    private LocalDateTime modifyDate;

    @Column(nullable = true)
    private Long viewCounts;

    @Column(nullable = false)
    private String communityCategory;

    @Column
    private Long recommend;

    @Builder.Default
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> communityImage = new ArrayList<>();


    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "memberId")
    private Member member;

    public void update(String title, String content){

        this.title = title;
        this.content = content;
    }

    public void increaseRecommend(){
        this.recommend += 1;
    }
    public void decreaseRecommend(){
        this.recommend -= 1;
    }
}
