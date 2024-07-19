package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.state.WriteStageDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class WriteStageDtoToStageMapper {

    @Autowired
    protected ProjectJpaRepository projectJpaRepository;

    @Mapping(target = "project", expression = "java(findProjectById(writeStageDto.getProjectId()))")
    public abstract Stage map(WriteStageDto writeStageDto);

    protected Project findProjectById(Long id) {
        return projectJpaRepository.findById(id).orElse(null);
    }
}
