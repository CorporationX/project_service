package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InternshipFilterDto {
    private InternshipStatus status;
    private long id;
}
