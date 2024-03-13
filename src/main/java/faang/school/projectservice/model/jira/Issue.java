package faang.school.projectservice.model.jira;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Issue extends JsonPrettyString {
    private String id;
    private String key;
    private IssueFields fields;
}
