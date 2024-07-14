package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    Internship toEntity(InternshipDto internshipDto);
    InternshipDto toDto(Internship internship);
}
