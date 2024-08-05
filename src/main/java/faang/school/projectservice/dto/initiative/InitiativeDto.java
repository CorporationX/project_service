package faang.school.projectservice.dto.initiative;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiativeDto {

    @NotNull(message = "Initiative ID cannot be null")
    private Long id;

    @NotBlank(message = "Initiative name cannot be blank")
    private String name;

    private String description;
    private Long curatorId;
    private String status;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
