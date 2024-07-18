package faang.school.projectservice.dto.project;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProjectCoverDto extends ProjectDto {
    @NotNull(message = "Cover image id should not be null")
    @NotBlank(message = "Cover image id should not be blank")
    @Length(min = 50, max = 61, message = "Cover image id length should be less than 61")
    private String coverImageId;
}
