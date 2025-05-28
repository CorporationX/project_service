package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MomentDto {
    private Long id;
    @NotBlank(message = "Name of Moment cannot be null")
    @Size(max = 128, message = "Name is too long")
    private String name;

    @Size(max = 4096, message = "Description is too long")
    private String description;

    @NotNull(message = "Date of Moment cannot be null")
    private LocalDateTime date;
    private List<Long> resourceIds;

    @NotNull(message = "Main projects ID cannot be null")
    private Long projectId;
    private List<Long> projectIds;
    private List<Long> userIds;
    private String imageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @Min(value = 1, message = "number of month must be from 1 to 12")
    @Max(value = 12, message = "number of month must be from 1 to 12")
    private Integer filterMonth;
    private List<Long> filterProjectIds;
}
