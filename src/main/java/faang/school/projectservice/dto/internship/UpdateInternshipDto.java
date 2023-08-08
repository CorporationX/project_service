package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class UpdateInternshipDto {
    @NotNull
    private Long id;
    private InternshipStatus status;
    private String name;
    private Long updatedBy;
}
