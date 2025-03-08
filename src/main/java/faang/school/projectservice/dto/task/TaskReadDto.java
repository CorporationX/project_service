package faang.school.projectservice.dto.task;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TaskReadDto {
    private long id;
    private String name;
    private String parentTaskName;
}
