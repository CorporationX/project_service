package faang.school.projectservice.dto.moment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentRestDto {
    private Long id;
    @NotEmpty(message = "Moment name can't be empty")
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    @NotEmpty(message = "Projects list for this moment can't be empty")
    private List<Long> projects;
    @NotEmpty(message = "UserIds list for this moment can't be empty")
    private List<Long> userIds;
}