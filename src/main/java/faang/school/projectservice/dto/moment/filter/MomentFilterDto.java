package faang.school.projectservice.dto.moment.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MomentFilterDto {

    private LocalDateTime createdAt;
    private LocalDateTime createdAtMin;
    private LocalDateTime createdAtMax;
    private String momentDescriptionPattern;

    //    partner-projects filter patterns
    private String projectNamePattern;
    private String projectDescriptionPattern;
}
