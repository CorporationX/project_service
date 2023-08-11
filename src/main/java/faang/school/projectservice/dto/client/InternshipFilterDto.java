package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternshipFilterDto {
    private InternshipStatus internshipStatus;
}
