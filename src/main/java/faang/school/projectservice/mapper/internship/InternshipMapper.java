package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {InternshipMapperHelper.class})
public interface InternshipMapper {
    @Mapping(source = "interns", target = "internsId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "project.id", target = "projectId")
    InternshipDto toDto(Internship internship);

    @Mapping(source = "internsId", target = "interns")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "projectId", target = "project.id")
    Internship toEntity(InternshipDto internshipDto);
}
