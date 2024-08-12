package faang.school.projectservice.dto.moment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MomentFilterDto {
    private LocalDateTime afterDateFilter;
    private LocalDateTime beforeDateFilter;
    private String projectFilter;
}
