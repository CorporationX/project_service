package faang.school.projectservice.dto.client;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VacancyDtoCreate {
    private String name;
    private String description;
    private Long projectId;
    private List<CandidateDto> candidates;
    private Integer count;
    private Long createdBy;
}
