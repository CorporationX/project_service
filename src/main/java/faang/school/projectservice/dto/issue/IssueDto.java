package faang.school.projectservice.dto.issue;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.joda.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueDto {
    String key;
    long typeId;

    @NonNull
    @Size(min = 1, max = 2000)
    String summary;

    @Size(max = 6000)
    String description;

    @NonNull
    @Future
    LocalDateTime dueDate;
}
