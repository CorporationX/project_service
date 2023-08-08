package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.Vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.Vacancy.ExtendedVacancyDto;
import faang.school.projectservice.dto.Vacancy.UpdateVacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
        VacancyMapper INSTANCE = Mappers.getMapper(VacancyMapper.class);

        @Mapping(target = "projectId", source = "project.id")
        ExtendedVacancyDto toDto(Vacancy vacancy);

        @Mapping(target = "project", ignore = true)
        Vacancy toEntity(CreateVacancyDto vacancyDto);

        @Mapping(target = "candidates", source = "candidates")
        @Mapping(target = "projectId", source = "project.id")
        void updateFromDto(UpdateVacancyDto vacancyDto, @MappingTarget Vacancy vacancy);
}
