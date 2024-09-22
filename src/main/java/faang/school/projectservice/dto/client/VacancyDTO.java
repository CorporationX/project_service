package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import lombok.Getter;

import java.util.List;


@Getter
public class VacancyDTO {
    private Long id;
    private String name;
    private String description;
    private Project project;
    private List<Candidate> candidates;
    private Integer count;
    private Long createdBy;
}
