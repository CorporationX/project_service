package faang.school.projectservice.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectDto {
    private Long projectId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String coverImageUrl;
}
