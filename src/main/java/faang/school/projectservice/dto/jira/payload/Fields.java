package faang.school.projectservice.dto.jira.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fields {
    private Project project;
    private String summary;
    private String description;
    private Issuetype issuetype;
}
