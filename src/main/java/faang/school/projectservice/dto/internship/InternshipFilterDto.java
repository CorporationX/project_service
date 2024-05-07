package faang.school.projectservice.dto.internship;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InternshipFilterDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
