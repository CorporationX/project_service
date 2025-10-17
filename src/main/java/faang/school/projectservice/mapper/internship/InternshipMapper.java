package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.model.Internship;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.TargetType;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    InternshipDto toDto(Internship internship);

    Internship update(UpdateInternshipDto dto, @MappingTarget Internship internship);

    Internship toEntity(CreateInternshipDto internshipDto);

    Internship toEntity(InternshipDto internshipDto);

    List<InternshipDto> toDtoList(List<Internship> internships);

    List<Internship> toEntityList(List<InternshipDto> internshipDtos);

}
