package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreateDto {

    @Null
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Project is required")
    private Long projectId;
}
