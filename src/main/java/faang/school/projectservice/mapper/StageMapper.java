package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StageMapper {

    @Mapping(target = "id", ignore = true)
    Stage toEntityCreate(StageRequestCreateDto stageRequestCreateDto, List<TeamMember> executors, Project project,
                         List<Task> tasks);

    StageRequestCreateDto toDto(Stage stage);
}