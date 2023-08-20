package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MomentDtoUpdate {
    @Min(1)
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private LocalDateTime date;
    private Long idProject;
}
