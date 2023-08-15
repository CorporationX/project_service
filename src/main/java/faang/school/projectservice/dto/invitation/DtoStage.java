package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtoStage {
    @Positive(message = "stage id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "stage id the value cannot be empty or greater than 9223372036854775807")
    private Long stageId;
}
