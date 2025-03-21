package faang.school.projectservice.dto.jira.response;

import faang.school.projectservice.dto.jira.Fields;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueGetResponseDto {

    private Fields fields;
}
