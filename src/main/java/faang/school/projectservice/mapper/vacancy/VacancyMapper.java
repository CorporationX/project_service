package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequestDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "createdBy", source = "authorId")
    @Mapping(target = "count", source = "requiredCandidatesCount")
    Vacancy toVacancy(OpenVacancyRequestDto openVacancyRequestDto);

    @Mapping(target = "candidates", ignore = true)
    VacancyResponseDto ToVacancyResponseDto(Vacancy vacancy);

    @Mapping(target = "candidates", ignore = true)
    List<VacancyResponseDto> ToVacancyResponseDtos(List<Vacancy> vacancies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "updatedBy", source = "teamMemberUpdaterId")
    @Mapping(target = "status", ignore = true)
    void update(@MappingTarget Vacancy vacancy, UpdateVacancyRequestDto updateVacancyRequestDto);
}
