package faang.school.projectservice.dto.client.internship;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.util.List;
@Data
public class InternshipUpdateDto {
    private Long id;
    private List<InternshipUserStatus> interns;
    private TeamRole role;
}
