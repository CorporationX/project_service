package faang.school.projectservice.dto.issue;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.joda.time.LocalDateTime;


@Data
@AllArgsConstructor
@Builder
public class IssueDto {
    private String key;
    private long typeId;

    @NonNull
    @Size(min = 1, max = 2000)
    private String summary;

    @Size(max = 6000)
    private String description;

    @NonNull
    @Future
    private LocalDateTime dueDate;
}
