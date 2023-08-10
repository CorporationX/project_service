package faang.school.projectservice.dto.internship;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResponseInternshipDto {
    @NotNull
    private Long id;
    @NotNull
    private Long projectId;
    @NotNull
    private Long mentorId;
    @NotEmpty
    @NotNull
    private List<Long> internIds;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;
    private InternshipStatus status;
    @NotBlank
    @Size(max = 4096, message = "Internship's description length can't be more than 4096 symbols")
    private String description;
    @NotBlank
    @Size(max = 128, message = "Internship's name length can't be more than 128 symbols")
    private String name;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private Long scheduleId;
}
