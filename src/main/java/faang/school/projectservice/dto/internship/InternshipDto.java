package faang.school.projectservice.dto.internship;


import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class InternshipDto {

    private Long id;//*

    private String name;//*
    @NotNull(message = "cannot be empty")
    private Long mentorId; // <- TeamMember mentorId
    private long projectId;// <- Project project
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Long> internsIds; //<-List<Interns> interns *
    private InternshipStatus status;// *
    private TeamRole role;//*
}
