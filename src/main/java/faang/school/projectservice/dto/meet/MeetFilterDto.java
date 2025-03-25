package faang.school.projectservice.dto.meet;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MeetFilterDto {

    @Size(max = 128)
    String titlePattern;
    LocalDate datePattern;
}
