package faang.school.projectservice.dto.invitation;

import faang.school.projectservice.exception.ValidException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DtoTeamMember {
    @Max(value = Long.MAX_VALUE, message = "id the value cannot  greater than 9223372036854775807")
    private Long id;
    @Positive(message = "user id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807")
    private long userId;

    public DtoTeamMember(Long id, long userId) {
        if (id != null) {
            if (id <= 0) {
                throw new ValidException("id cannot be less than 0");
            }
        }
        this.id = id;
        this.userId = userId;
    }
}
