package faang.school.projectservice.dto.moment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MomentDto {
    @NonNull
    private String name;
    @NonNull
    private LocalDateTime date;
    private Long idProject;
}
