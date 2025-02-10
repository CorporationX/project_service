package faang.school.projectservice.dto.coverImageVacancy;

import faang.school.projectservice.model.ResourceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
    private Long id;
    private ResourceStatus status;
    private LocalDateTime createdAt;
}
