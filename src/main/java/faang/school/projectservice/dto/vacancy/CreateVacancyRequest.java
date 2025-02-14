package faang.school.projectservice.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.WorkSchedule;
import lombok.Data;

import java.util.List;

@Data
public class CreateVacancyRequest {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private TeamRole position;
    @JsonProperty
    private Long projectId;
    @JsonProperty
    private Long createdBy;
    @JsonProperty
    private Double salary;
    @JsonProperty
    private WorkSchedule workSchedule;
    @JsonProperty
    private Integer count;
    @JsonProperty
    private List<Long> requiredSkillIds;
}
