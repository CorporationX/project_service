package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageCreateDto;
import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.repository.query.parser.Part;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageMapper {

    Stage toEntityCreate(StageCreateDto stageCreateDto, List<TeamMember> executors, Project project,
                         List<Task> tasks);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "taskId", source = "tasks.id")
    @Mapping(target = "teamMemberId", source = "executors.id")
    StageDto toDto(Stage stage);


    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "taskId", source = "tasks.id")
    @Mapping(target = "teamMemberId", source = "executors.id")
    List<StageDto> toListDto(List<Stage> stageList);


}