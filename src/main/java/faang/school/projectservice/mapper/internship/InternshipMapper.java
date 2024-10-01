package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TeamMemberMapper.class, ProjectMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InternshipMapper {

    @Mapping(source = "project.id", target = "projectId")
    InternshipDto toDto(Internship internship);

    @Mapping(source = "projectId", target = "project.id")
    Internship toEntity(InternshipDto internshipDto);
}
