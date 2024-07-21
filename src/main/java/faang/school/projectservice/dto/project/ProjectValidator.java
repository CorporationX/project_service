package faang.school.projectservice.dto.project;

import java.util.List;

public interface ProjectValidator {
    List<Long> getStagesIds();
    List<Long> getTeamsIds();
}
