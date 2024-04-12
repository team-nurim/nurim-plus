package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @Column
    private String replyer;

    @Column
    private String replyText;

    @Column
    private Long replyRecommend;

    @CreationTimestamp
    private LocalDateTime replyRegisterDate;

    @UpdateTimestamp
    private LocalDateTime replyModifyDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "communityId")
    private Community community;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "memberId")
    private Member member;



    public void update(String replyText){
        this.replyText = replyText;
    }
}
