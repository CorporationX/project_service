package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipToCreateDto;
import faang.school.projectservice.dto.client.InternshipToUpdateDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;


import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TeamMemberMapper.class)
public interface InternshipMapper {

    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "internsId", target = "interns")
    Internship toEntity(InternshipToCreateDto internshipDto);

    @Named("toDto")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "internsId", qualifiedByName = "internsById")
    InternshipDto toDto(Internship internship);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "mentorId", target = "mentorId.id", ignore = true)
    @Mapping(target = "interns", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    void updateEntity(InternshipToUpdateDto internshipDto, @MappingTarget Internship internship);

    @IterableMapping(qualifiedByName = "toDto")
    List<InternshipDto> toDtoList(List<Internship> internships);

    @Named("internsById")
    default List<Long> internsById(List<TeamMember> internships) {
        return internships.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
