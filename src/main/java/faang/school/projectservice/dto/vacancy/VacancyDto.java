package faang.school.projectservice.dto.vacancy;

import faang.school.projectservice.model.Candidate;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VacancyDto {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String position;
    private String description;

    private Long projectId;

    private List<Candidate> candidates;

    @NotNull
    private String status;

    private Integer count;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;
}
