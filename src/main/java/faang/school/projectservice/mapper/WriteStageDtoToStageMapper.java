package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.state.WriteStageDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class WriteStageDtoToStageMapper {

    @Autowired
    protected ProjectJpaRepository projectJpaRepository;

    @Mapping(target = "project", expression = "java(findProjectById(writeStageDto.getProjectId()))")
    public abstract Stage map(WriteStageDto writeStageDto);

    protected Project findProjectById(Long id) {
        return projectJpaRepository.findById(id).orElseThrow(() -> {
            log.error(String.format("WriteStageDtoToStageMapper.findProjectById: Project with id %s not found", id));
            return new IllegalArgumentException("Project with id " + id + " not found");
        });
    }
}
