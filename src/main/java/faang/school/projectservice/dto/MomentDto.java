package faang.school.projectservice.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MomentDto {
    @NonNull
    private String name;
    @NonNull
    private LocalDateTime date;
    private Long idProject;
}
