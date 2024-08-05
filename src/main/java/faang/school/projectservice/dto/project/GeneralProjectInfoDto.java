package faang.school.projectservice.dto.project;

import java.util.List;

public interface GeneralProjectInfoDto {
    List<Long> getStagesIds();
    List<Long> getTeamsIds();
}
