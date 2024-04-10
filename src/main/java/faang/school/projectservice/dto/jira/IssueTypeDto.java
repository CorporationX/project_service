package faang.school.projectservice.dto.jira;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueTypeDto {
    private URI self;
    private Long id;
    private String name;
    private boolean isSubtask;
    private String description;
    private URI iconUri;
}
