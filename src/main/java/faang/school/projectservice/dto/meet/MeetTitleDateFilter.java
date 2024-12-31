package faang.school.projectservice.dto.meet;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetTitleDateFilter {
    @NotNull
    private String title;
    @NotNull
    private LocalDateTime minDate;
    @NotNull
    private LocalDateTime maxDate;
}
