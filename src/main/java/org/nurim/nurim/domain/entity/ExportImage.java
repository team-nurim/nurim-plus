package org.nurim.nurim.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExportImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exportImageId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exportId", unique = true)
    private Export export;

    @Column(length = 500)
    private String exportProfileImage;
}
