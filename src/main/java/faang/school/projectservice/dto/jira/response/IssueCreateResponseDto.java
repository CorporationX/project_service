package faang.school.projectservice.dto.jira.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueCreateResponseDto {
    private String id;
    private String key;
}
