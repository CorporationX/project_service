package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Getter;

@Getter
public class InternshipFilterDto {
    private InternshipStatus status;
}
