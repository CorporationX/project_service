package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record InternshipFilterRequest(@NotBlank List<TeamRole> roles, @NotBlank InternshipStatus status) {

}
