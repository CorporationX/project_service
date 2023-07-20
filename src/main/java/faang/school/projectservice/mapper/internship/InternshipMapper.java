package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {
    InternshipDto toDto(Internship internship);

    InternshipDto toEntity(InternshipDto internshipDto);
}
