package faang.school.projectservice.dto.internship;

import faang.school.projectservice.controller.Marker;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternshipDto {

    @Null(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnUpdate.class, message = "Internship project field can't be null!")
    private Long id;
    private Long projectId;
    private TeamMemberDto mentorId;

    @NotNull(message = "Internship can't be created without interns!")
    private List<TeamMemberDto> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String name;
    private InternshipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
