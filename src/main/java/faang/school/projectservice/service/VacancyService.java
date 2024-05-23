package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.filter.VacancyFilterService;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyFilterService vacancyFilterService;

    public VacancyDto createVacancy(VacancyDto vacancy) {
        vacancyValidator.validateVacancyName(vacancy);
        vacancyValidator.validateCountOfVacancy(vacancy);
        vacancyValidator.checkExistsById(vacancy.getId());

        TeamMember teamMember = teamMemberRepository.findById(vacancy.getCreatedBy());

        vacancyValidator.validateTeamMember(teamMember);

        Vacancy convertedVacancy = vacancyMapper.toEntity(vacancy);
        return vacancyMapper.toDto(vacancyRepository.save(convertedVacancy));
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        vacancyValidator.checkExistsVacancy(vacancyDto);

        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyDto.getId());

        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            vacancyValidator.validateCountOfCandidate(vacancy);
            vacancyValidator.checkIfAllCandidatesHaveStatusAccepted(vacancy);

            Vacancy savedVacancy = vacancyRepository.save(vacancy);
            return vacancyMapper.toDto(savedVacancy);
        }

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(savedVacancy);
    }

    public void deleteVacancy(VacancyDto vacancy) {
        vacancyValidator.checkExistsVacancy(vacancy);
        Vacancy vacancyForDelete = vacancyRepository.getReferenceById(vacancy.getId());
        List<Long> candidatesIds = vacancyForDelete.getCandidates().stream().map(Candidate::getId).toList();
        teamMemberRepository.deleteAllById(candidatesIds);
        vacancyRepository.deleteById(vacancy.getId());
    }

    public List<VacancyDto> getVacanciesWithFilter(VacancyFilterDto vacancyFilter) {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        return vacancyFilterService.applyFilter(vacancies.stream(), vacancyFilter)
                .map(vacancyMapper::toDto)
                .toList();
    }

    public VacancyDto getVacancy(long id) {
        vacancyValidator.checkExistsById(id);
        return vacancyMapper.toDto(vacancyRepository.getReferenceById(id));
    }
}