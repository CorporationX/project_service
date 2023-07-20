package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class InternshipDto {
    private Long id;
    private Long project;
    private Long mentort;
    private List<Long> interns;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long schedule;
}
