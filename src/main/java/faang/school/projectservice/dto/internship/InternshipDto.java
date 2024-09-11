package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;

import java.time.LocalDateTime;

public record InternshipDto(String internshipName,
                            String description,
                            Long mentorId,
                            Long projectId,
                            InternshipStatus status,
                            LocalDateTime endDate) {
}