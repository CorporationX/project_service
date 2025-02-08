package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacancyMapper {
    @Mapping(source = "creatorId", target = "createdBy")
    Vacancy toEntity(VacancyDto dto);

    @Mapping(source = "createdBy", target = "creatorId")
    VacancyDto toDto(Vacancy entity);
}
