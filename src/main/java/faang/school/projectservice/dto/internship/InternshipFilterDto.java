package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Data;

@Data
public class InternshipFilterDto {
    private InternshipStatus status;
}
