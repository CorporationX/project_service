package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InternshipUpdateDto {
    @NotNull(message = "Internship ID is required")
    private Long id;
    private Long mentorId;
    private String description;
    private String name;
    private Long scheduleId;
    private LocalDateTime endDate;
    private InternshipStatus status;
    private List<InternshipParticipantUpdateDto> interns;
}
