package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface InternshipMapper {

    InternshipDto toDto(Internship internship);

    Internship toEntity(InternshipDto dto);

    List<InternshipDto> toDtoList(List<Internship> internships);

    List<Internship> toEntityList(List<InternshipDto> dtos);
}
