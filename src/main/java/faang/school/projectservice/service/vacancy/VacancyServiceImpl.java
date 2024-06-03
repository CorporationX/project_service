package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.service.vacancy.filters.VacancyFilterService;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final VacancyValidator vacancyValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyFilterService vacancyFilterService;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        vacancyValidator.validateVacancyName(vacancyDto);
        vacancyValidator.validateCountOfVacancy(vacancyDto);
        vacancyValidator.checkExistsById(vacancyDto.getId());

        TeamMember teamMember = teamMemberRepository.findById(vacancyDto.getCreatedBy());

        vacancyValidator.validateTeamMember(teamMember);

        Vacancy convertedVacancy = vacancyMapper.toEntity(vacancyDto);
        return vacancyMapper.toDto(vacancyRepository.save(convertedVacancy));
    }

    public VacancyDto updateVacancy(VacancyDto vacancyDto) {
        vacancyValidator.checkExistsVacancy(vacancyDto);

        Vacancy vacancy = vacancyRepository.getReferenceById(vacancyDto.getId());

        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            vacancyValidator.validateCountOfCandidate(vacancy);
            vacancyValidator.checkAcceptedCandidatesCount(vacancy);
        }

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(savedVacancy);
    }

    public void deleteVacancy(VacancyDto vacancyDto) {
        vacancyValidator.checkExistsVacancy(vacancyDto);

        Vacancy vacancyForDelete = vacancyRepository.getReferenceById(vacancyDto.getId());
        List<Long> candidatesIds = vacancyForDelete.getCandidates().stream().map(Candidate::getId).toList();

        deleteCandidatesNotAccepted(candidatesIds);
        vacancyRepository.deleteById(vacancyDto.getId());
    }

    public List<VacancyDto> getVacanciesWithFilter(VacancyFilterDto vacancyFilterDto) {
        List<Vacancy> vacancies = vacancyRepository.findAll();

        return vacancyFilterService.applyFilter(vacancies.stream(), vacancyFilterDto)
                .map(vacancyMapper::toDto)
                .toList();
    }

    public VacancyDto getVacancy(long id) {
        vacancyValidator.checkExistsById(id);
        return vacancyMapper.toDto(vacancyRepository.getReferenceById(id));
    }

    private void deleteCandidatesNotAccepted(List<Long> candidatesIds) {
        List<Long> candidatesForDelete = new ArrayList<>();

        for (Long candidateId : candidatesIds) {
            Candidate candidate = candidateRepository.getReferenceById(candidateId);
            if (candidate.getCandidateStatus() != CandidateStatus.ACCEPTED){
                candidatesForDelete.add(candidateId);
            }
        }
        teamMemberRepository.deleteAllById(candidatesForDelete);
    }
}