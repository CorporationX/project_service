package faang.school.projectservice.mapper.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = StageRolesMapper.class, injectionStrategy = InjectionStrategy.FIELD)
public interface StageMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(target = "executorIds", source = "executors", qualifiedByName = "toExecutorIds")
    StageDto toDto(Stage stage);

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(target = "executors", source = "executorIds", qualifiedByName = "toExecutors")
    Stage toEntity(StageDto stageDto);

    @Named(value = "toExecutors")
    default List<TeamMember> toExecutors(List<Long> executorIds) {
        if (executorIds == null) {
            return null;
        }

        List<TeamMember> executors = new ArrayList<>();
        for (Long executorId : executorIds) {
            executors.add(TeamMember.builder().id(executorId).build());
        }

        return executors;
    }

    @Named(value = "toExecutorIds")
    default List<Long> toExecutorIds(List<TeamMember> executors) {
        if (executors == null) {
            return null;
        }

        List<Long> executorIds = new ArrayList<>();
        for (TeamMember executor : executors) {
            executorIds.add(executor.getId());
        }

        return executorIds;
    }
}
