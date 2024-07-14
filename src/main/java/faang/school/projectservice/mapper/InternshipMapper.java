package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    Internship toEntity(InternshipDto internshipDto);
    InternshipDto toDto(Internship internship);

    void updateEntity(InternshipDto internshipDto, @MappingTarget Internship internship);

    List<InternshipDto> toDtoList(List<Internship> internships);
}
