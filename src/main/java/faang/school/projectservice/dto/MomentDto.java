package faang.school.projectservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MomentDto {
    private Long id;
    @NotBlank(message = "Name should not be empty.")
    private String name;
    @NotBlank(message = "Name should not be empty.")
    private String description;
    private LocalDateTime date;
    private List<Long> projects;
    private List<Long> userIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
