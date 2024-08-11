package faang.school.projectservice.dto.task.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskFilterDto {
    private String keyWord;
    private String status;
    private Long performerUserId;
}
