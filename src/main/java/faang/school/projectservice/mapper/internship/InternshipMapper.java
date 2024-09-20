package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = InternshipMapperHelper.class)
public interface InternshipMapper {
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "internsId", target = "interns", qualifiedByName = "mapToInterns")
    Internship toEntity(InternshipDto internshipDto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns", target = "internsId", qualifiedByName = "mapToInternId")
    InternshipDto toDto(Internship internship);
}
