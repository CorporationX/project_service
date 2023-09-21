package faang.school.projectservice.dto.internship;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CreateInternshipDto {
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
    @NotBlank
    @Size(max = 128, message = "Internship's name length can't be more than 128 symbols")
    private String name;
    @NotNull
    private TeamRole internshipRole;
    private Long createdBy;
}
