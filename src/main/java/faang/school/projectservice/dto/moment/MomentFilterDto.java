package faang.school.projectservice.dto.moment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MomentFilterDto {
    private long month;
    private long year;
    private List<Long> projectId;
}
