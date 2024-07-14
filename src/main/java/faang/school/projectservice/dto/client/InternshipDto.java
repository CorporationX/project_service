package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.InternshipStatus;

import java.time.LocalDateTime;
import java.util.List;

public class InternshipDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> internsId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
}