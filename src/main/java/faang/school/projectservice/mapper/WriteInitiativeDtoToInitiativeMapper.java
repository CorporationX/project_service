package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {WriteStageDtoToStageMapper.class})
public abstract class WriteInitiativeDtoToInitiativeMapper {

    @Autowired
    protected ProjectJpaRepository projectJpaRepository;
    @Autowired
    protected TeamMemberJpaRepository teamMemberJpaRepository;

    @Mappings({
            @Mapping(source = "writeStageDtos", target = "stages"),
            @Mapping(target = "project", expression = "java(findProjectById(writeInitiativeDto.getProjectId()))"),
            @Mapping(target = "curator", expression = "java(findTeamMemberById(writeInitiativeDto.getCuratorId()))")
    })
    public abstract Initiative map(WriteInitiativeDto writeInitiativeDto);

    @Mappings({
            @Mapping(source = "writeStageDtos", target = "stages"),
            @Mapping(target = "project", expression = "java(findProjectById(writeInitiativeDto.getProjectId()))"),
            @Mapping(target = "curator", expression = "java(findTeamMemberById(writeInitiativeDto.getCuratorId()))")
    })
    public abstract Initiative map(WriteInitiativeDto writeInitiativeDto, @MappingTarget Initiative initiative);

    protected Project findProjectById(Long id) {
        return projectJpaRepository.findById(id).orElse(null);
    }

    protected TeamMember findTeamMemberById(Long id) {
        return teamMemberJpaRepository.findById(id).orElse(null);
    }

}
