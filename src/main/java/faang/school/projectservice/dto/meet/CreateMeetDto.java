package faang.school.projectservice.dto.meet;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateMeetDto {
    @NotBlank(message = "Meet title must not be blank")
    @Size(max = 128, message = "Meet title can't be longer then 128 symbols")
    private String title;

    @NotBlank(message = "Meet description must not be blank")
    @Size(max = 512, message = "Meet description can't be longer then 512 symbols")
    private String description;

    @Positive(message = "Meet must have project id")
    private long projectId;

    @Future(message = "Meet must have future date")
    private LocalDateTime startsAt;
}
