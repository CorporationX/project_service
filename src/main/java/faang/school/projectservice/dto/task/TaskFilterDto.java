package faang.school.projectservice.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskFilterDto {

    private String status;
    private Long performerId;
    private String keyword;
}
