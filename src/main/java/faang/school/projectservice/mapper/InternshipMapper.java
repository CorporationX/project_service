package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "status", ignore = true)
    Internship toInternship(CreateInternshipDto createInternshipDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(target = "internsIds", expression = "java(getInternsIds(internship))")
    InternshipDto toInternshipDto(Internship internship);

    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    void update(UpdateInternshipDto dto, @MappingTarget Internship entity);

    default List<Long> getInternsIds(Internship internship) {
        if (internship.getInterns() == null) {
            return List.of();
        }
        return internship.getInterns().stream().map(TeamMember::getId).toList();
    }
}