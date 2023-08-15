package faang.school.projectservice.dto.internship;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipDto {

        @NotNull(message = "Project id is required")
        Long projectId;

        @NotNull(message = "Mentor id is required")
        Long mentorId;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        List<Long> internIds;

        @NotNull(message = "Start date is required")
        LocalDateTime startDate;

        @NotNull(message = "End date is required")
        LocalDateTime endDate;

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        InternshipStatus status;

        @NotBlank(message = "Internship's description is required")
        String description;

        @NotBlank(message = "Internship's name is required")
        String name;
}
