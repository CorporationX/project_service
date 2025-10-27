package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface InternshipMapper {

    InternshipMapper INSTANCE = Mappers.getMapper(InternshipMapper.class);

    @Mapping(target = "name", source = "entity.name") //
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Internship toInternship(InternshipDto internshipDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "internsIds", expression = "java(mapInternsToIds(internship.getInterns()))")
    InternshipDto toInternshipDto(Internship internship);

    default List<Long> mapInternsToIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).collect(Collectors.toList());
    }
}