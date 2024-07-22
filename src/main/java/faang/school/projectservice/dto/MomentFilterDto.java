package faang.school.projectservice.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MomentFilterDto {
    @Size(max = 255, message = "Name pattern should not exceed 255 characters")
    private String namePattern;
    private List<Long> projectsPattern;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
