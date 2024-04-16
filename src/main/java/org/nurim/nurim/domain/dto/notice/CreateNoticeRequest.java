package org.nurim.nurim.domain.dto.notice;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CreateNoticeRequest {

    @NotEmpty
    private String noticeTitle;
    @NotEmpty
    private String noticeContent;
    @NotEmpty
    private String noticeWriter;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate noticeRegisterDate;

}
