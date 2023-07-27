package faang.school.projectservice.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MomentDto {
    @Min(1)
    private Long id;
    @NonNull
    private String name;
    @NonNull
    private LocalDateTime date;
}
