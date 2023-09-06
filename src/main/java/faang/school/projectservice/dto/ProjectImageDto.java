package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectImageDto {
    private Long projectId;
    private String fileId;
}
