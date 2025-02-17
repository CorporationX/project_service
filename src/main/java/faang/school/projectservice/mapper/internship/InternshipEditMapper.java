package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipEditMapper extends InternshipMapper {
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentorId.id", target = "mentorId")
    @Mapping(source = "interns",
            target = "internsIds",
            qualifiedByName = "mapToIds",
            conditionExpression = "java(internship.getInterns() != null)")
    InternshipEditDto toDto(Internship internship);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "mentorId", ignore = true)
    @Mapping(target = "interns", ignore = true)
    Internship toEntity(InternshipEditDto internshipDto);
}
