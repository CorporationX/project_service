package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;


@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface VacancyMapper {

    default Vacancy toVacancy(VacancyCreateDto vacancyCreateDto, Project project) {
        if (vacancyCreateDto == null) {
            return null;
        }
        return Vacancy.builder()
                .name(vacancyCreateDto.name())
                .description(vacancyCreateDto.description())
                .position(vacancyCreateDto.position())
                .count(vacancyCreateDto.count())
                .project(project)
                .build();
    }

    VacancyDto toVacancyDto(Vacancy vacancy);

    default void updateVacancyFromDto(VacancyUpdateDto vacancyUpdateDto, Vacancy vacancy) {
        if (vacancyUpdateDto == null || vacancy == null) {
            return;
        }

        if (StringUtils.hasText(vacancyUpdateDto.name())) {
            vacancy.setName(vacancyUpdateDto.name());
        }

        if (vacancyUpdateDto.description() != null && !vacancyUpdateDto.description().isEmpty()) {
            vacancy.setDescription(vacancyUpdateDto.description());
        }

        if (vacancyUpdateDto.count() != null) {
            vacancy.setCount(vacancyUpdateDto.count());
        }

        if (vacancyUpdateDto.position() != null) {
            vacancy.setPosition(vacancyUpdateDto.position());
        }
    }
}
