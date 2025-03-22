package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.Candidate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyUpdateDto {
    @NotNull
    private Long id;
    private String name;
    private String position;
    private String description;
    private Long projectId;
    private List<Candidate> candidates;
    private String status;
    private Integer count;
    private LocalDateTime updatedAt;
}
