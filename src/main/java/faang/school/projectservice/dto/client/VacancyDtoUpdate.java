package faang.school.projectservice.dto.client;

import java.util.List;

public class VacancyDtoUpdate {
    private Long id;
    private String name;
    private String description;
    private Long projectId;
    private List<CandidateDto> candidates;
    private Integer count;
    private Long createdBy;
}
