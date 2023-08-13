package faang.school.projectservice.dto.subproject;

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
    private String statusFilter;
}
