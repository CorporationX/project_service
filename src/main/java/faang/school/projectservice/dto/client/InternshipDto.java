package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InternshipDto {
    private List<Long> interns;
    private TaskStatus taskStatus;
}
