package faang.school.projectservice.dto.jira.request;

import faang.school.projectservice.dto.jira.Fields;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueRequestDto {
    private Fields fields;
}