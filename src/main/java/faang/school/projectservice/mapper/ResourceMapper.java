package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ResourceMapper.NestedMapper.class, ProjectMapper.class})
public interface ResourceMapper {

    ResourceDto toDto(Resource resource);

    Resource toEntity(ResourceDto dto);

    List<ResourceDto> toDtoList(List<Resource> resources);

    List<Resource> toEntityList(List<ResourceDto> dtos);

    // дополнительные маппинги, если нужно мапить только id при toEntity — используй @Named методы
    @Named("teamMemberIdOnly")
    static TeamMember teamMemberIdOnly(TeamMemberDto dto) {
        if (dto == null || dto.getId() == null) return null;
        TeamMember tm = new TeamMember();
        tm.setId(dto.getId());
        return tm;
    }

    @Named("projectIdOnly")
    static Project projectIdOnly(ProjectDto dto) {
        if (dto == null || dto.getId() == null) return null;
        Project p = new Project();
        p.setId(dto.getId());
        return p;
    }

    @Mapper(componentModel = "spring")
    interface NestedMapper {
        TeamMemberDto toDto(TeamMember m);
        TeamMember toEntity(TeamMemberDto dto);

        // MapStruct сам корректно мапит List<Enum>, BigInteger, LocalDateTime и т.д.
    }
}