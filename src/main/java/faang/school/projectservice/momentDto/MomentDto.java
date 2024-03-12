package faang.school.projectservice.momentDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime date;
}
