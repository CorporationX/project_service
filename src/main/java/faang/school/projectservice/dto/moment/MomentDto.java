package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class MomentDto {
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private List<Long> projectIds;
    private Long createdBy;
    private LocalDateTime date;
    private List<Long> memberIds;
    private List<Long> resourceIds;
    private String imageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
