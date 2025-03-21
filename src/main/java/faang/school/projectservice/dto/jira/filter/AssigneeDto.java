package faang.school.projectservice.dto.jira.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeDto {
    private String accountId;
    private String name;
}
