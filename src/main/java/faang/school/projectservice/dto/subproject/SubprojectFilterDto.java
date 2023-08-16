package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubprojectFilterDto {
    @NotNull
    private Long id;
    @NotNull
    private Long requesterId;
    private String nameFilter;
    private ProjectStatus statusFilter;
}
