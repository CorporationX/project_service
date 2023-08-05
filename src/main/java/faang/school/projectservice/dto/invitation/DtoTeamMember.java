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
    @Positive(message = "user id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807")
    private long userId;
}
