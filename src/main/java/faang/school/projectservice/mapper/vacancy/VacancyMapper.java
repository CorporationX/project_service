package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.dto.vacancy.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface VacancyMapper {

    Vacancy toVacancy(VacancyCreateDto vacancyCreateDto);

    VacancyDto toVacancyDto(Vacancy vacancy);

    Candidate toCandidate(CandidateCreateDto candidateCreateDto);

    CandidateDto toCandidateDto(Candidate candidate);

    default void updateVacancyFromDto(VacancyUpdateDto vacancyUpdateDto, Vacancy vacancy) {
        if (vacancyUpdateDto == null || vacancy == null) {
            return;
        }

        if (vacancyUpdateDto.description() != null) {
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
