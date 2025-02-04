package faang.school.projectservice.dto.meet;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateMeetDto {
    @Positive
    private long id;

    @NotNull(message = "Meet must have title!")
    @NotBlank(message = "Meet title must not be blank")
    @Size(max = 128, message = "Meet title can't be longer then 128 symbols")
    private String title;

    @NotNull(message = "Meet must have description!")
    @NotBlank(message = "Meet description must not be blank")
    @Size(max = 512, message = "Meet description can't be longer then 512 symbols")
    private String description;

    @Future(message = "Meet must have future date")
    private LocalDateTime startsAt;
}
