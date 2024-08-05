package faang.school.projectservice.dto.internship;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InternshipDto {
    private Long id;
    @NotNull(message = "projectId can't be null")
    private Long projectId;
    @NotNull(message = "mentorId can't be null")
    private Long mentorId;
    @NotEmpty(message = "internIds can't be empty")
    private List<Long> internIds;
    @NotNull(message = "startDate can't be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime startDate;
    @NotNull(message = "endDate can't be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime endDate;
    @NotNull(message = "status can't be null")
    private InternshipStatus status;
    private String description;
    private String name;
    @NotNull(message = "createdBy can't be null")
    private Long createdBy;
    @NotNull(message = "updatedBy can't be null")
    private Long updatedBy;
    @NotNull(message = "scheduleId can't be null")
    private Long scheduleId;
}
