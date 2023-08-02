package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DtoStage {
    @Positive(message = "stage id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "stage id the value cannot be empty or greater than 9223372036854775807")
    private long stageId;
    @NotBlank(message = "stage name can't be empty")
    @Size(min = 1, max = 1000, message = "stage name the value cannot be empty or more than 100 characters")
    private String stageName;
}
