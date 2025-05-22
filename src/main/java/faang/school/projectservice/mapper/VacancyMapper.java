package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = CandidateMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "vacancy.candidates", target = "candidates")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(source = "dto.position", target = "position")
    @Mapping(source = "dto.count", target = "count")
    @Mapping(source = "dto.status", target = "status")
    Vacancy toEntity(VacancyDto dto);

}
