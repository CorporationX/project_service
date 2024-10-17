package faang.school.projectservice.model.dto.jira;

import faang.school.projectservice.validator.groups.ChangeStatusGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JiraStatusDto {
    private String id;

    @NotBlank(groups = ChangeStatusGroup.class)
    private String name;
}
