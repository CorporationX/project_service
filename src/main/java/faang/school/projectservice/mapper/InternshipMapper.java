package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(target = "internsId", source = "interns.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "mentorId", source = "mentorId.id")
    Internship toInternship(InternshipDto internshipDto);

    @Mapping(target = "internsId", source = "interns.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "mentorId", source = "mentorId.id")
    //на что менять статус enum
    @Mapping(target = "schedule", ignore = true)

    InternshipDto toInternshipDto(Internship internship);
}
