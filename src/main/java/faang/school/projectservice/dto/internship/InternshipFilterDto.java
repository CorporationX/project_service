package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InternshipFilterDto {
    private InternshipStatus status;
}
