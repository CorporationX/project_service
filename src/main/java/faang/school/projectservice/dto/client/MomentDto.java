package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MomentDto {
    private Long id;
    private String name;
    private List<Long> projectIds;
    private List<Long> userIds;
    private LocalDateTime date;
}
