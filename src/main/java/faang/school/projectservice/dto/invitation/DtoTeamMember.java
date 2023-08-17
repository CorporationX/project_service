package faang.school.projectservice.dto.invitation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DtoTeamMember {
    @Positive(message = "user id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "id the value cannot  greater than 9223372036854775807")
    private Long id;
}
