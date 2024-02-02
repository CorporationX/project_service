package faang.school.projectservice.dto.project;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSubProjectDto {
    @NotNull(message = "the parentId field must not be null")
    private Long parentId;
    private String name;
    private String description;
}