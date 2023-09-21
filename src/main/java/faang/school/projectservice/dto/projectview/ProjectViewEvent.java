package faang.school.projectservice.dto.projectview;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ProjectViewEvent {
    private Long projectId;
    private Long userId;
    private Date timestamp;
}
