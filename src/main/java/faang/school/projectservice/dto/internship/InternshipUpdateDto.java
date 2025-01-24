package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.util.List;
@Data
public class InternshipUpdateDto {
    private Long id;
    private Long mentorId;
    private List<InternshipUserStatusDto> interns;
    private TeamRole role;
    private InternshipStatus status;
}
