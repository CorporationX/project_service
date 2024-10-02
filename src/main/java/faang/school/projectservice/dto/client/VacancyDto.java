package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Vacancy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@NoArgsConstructor
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<CandidateDto> candidates;
    private Integer count;
    private Long createdBy;

    public VacancyDto(Vacancy vacancy) {
        this.id = vacancy.getId();
        this.name = vacancy.getName();
        this.description = vacancy.getDescription();
        this.projectId = vacancy.getProject() != null ? vacancy.getProject().getId() : null;
        this.count = vacancy.getCount();
        this.createdBy = vacancy.getCreatedBy();
        this.candidates = vacancy.getCandidates().stream()
                .map(candidate -> new CandidateDto(candidate))
                .collect(Collectors.toList());
    }
}
