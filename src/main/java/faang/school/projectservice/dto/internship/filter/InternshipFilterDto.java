package faang.school.projectservice.dto.internship.filter;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InternshipFilterDto {
    private String namePattern;
    private LocalDateTime startDatePattern;
    private LocalDateTime endDatePattern;
    private InternshipStatus statusPattern;
}