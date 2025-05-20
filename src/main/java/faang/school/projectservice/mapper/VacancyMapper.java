package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = CandidateMapper.class)
public interface VacancyMapper {

    VacancyMapper INSTANCE = Mappers.getMapper(VacancyMapper.class);

    @Mapping(source = "vacancy.candidates", target = "candidates")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "candidates", ignore = true)
    @Mapping(source = "dto.position", target = "position")
    @Mapping(source = "dto.count", target = "count")
    @Mapping(source = "dto.status", target = "status")
    Vacancy toEntity(VacancyDto dto);

}
