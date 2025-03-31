package faang.school.projectservice.dto.meet;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class MeetFilterDto {
    private String title;
    private LocalDateTime createdAt;
}
