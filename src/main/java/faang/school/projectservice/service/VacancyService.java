package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.exceptions.EntityNotFoundException;
import faang.school.projectservice.filters.filters.VacancyFilterable;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final CandidateRepository candidateRepository;
    private final List<VacancyFilterable> vacancyFilters;

    public VacancyDto getVacancy(Long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        if(vacancy.isEmpty()) {
            throw new EntityNotFoundException("Not found");
        }
        return vacancyMapper.toDto(vacancy.get());
    }

    public List<VacancyDto> getAll(final VacancyFilterDto filterDto) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        List<VacancyFilterable> filters = vacancyFilters.stream().filter(el -> el.isValid(filterDto)).toList();
        for (VacancyFilterable filter : filters) {
            vacancies = filter.apply(vacancies, filterDto);
        }
        return vacancies.map(vacancyMapper::toDto).toList();
    }

    public Map<String, String> create(VacancyDto vacancyDto) throws ValidationException {
        TeamMember memberInCharge = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        if (memberInCharge == null) {
            throw new EntityNotFoundException("Team member not found");
        }
        isHrValidation(memberInCharge);
        Vacancy createdVacancy = vacancyRepository
                .create(vacancyDto.getName(), vacancyDto.getDescription(), vacancyDto.getProjectId(),
                        vacancyDto.getCreatedBy(), vacancyDto.getUpdatedBy(), vacancyDto.getStatus().toString(),
                        vacancyDto.getSalary(), vacancyDto.getWorkSchedule().toString(), vacancyDto.getCount());

        attachCandidates(vacancyDto, createdVacancy);
        return Map.of("message", "vacancy created",
                "status", HttpStatus.CREATED.toString());
    }

    private void attachCandidates(VacancyDto vacancyDto, Vacancy createdVacancy) {
        vacancyDto.getCandidateIds().forEach((candidateId) -> {
            Optional<Candidate> candidateToEdit = candidateRepository.findById(candidateId);
            candidateToEdit.ifPresentOrElse(
                    (candidate) -> {
                        candidate.setVacancy(createdVacancy);
                        candidateRepository.save(candidate);
                    },
                    () -> {
                        throw new EntityNotFoundException("Candidate not found");
                    });
        });
    }

    private static void isHrValidation(TeamMember memberInCharge) throws ValidationException {
        boolean isHr = memberInCharge.getRoles().contains(TeamRole.OWNER) || memberInCharge.getRoles().contains(TeamRole.MANAGER);
        if (!isHr) {
            throw new ValidationException("User must be either manager or owner");
        }
    }

    public Map<String, String> update(Long id, VacancyDto vacancyDto) throws ValidationException {
        Vacancy editVacancy = getEditedVacancy(id);
        if (
                vacancyDto.getStatus().equals(VacancyStatus.CLOSED)
                        && editVacancy.getCount() > editVacancy.getCandidates().size()
        ) {
            throw new ValidationException("Not enough candidates");
        }
        vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return Map.of("message", "vacancy updated",
                "status", HttpStatus.OK.toString());
    }

    private Vacancy getEditedVacancy(Long id) {
        Optional<Vacancy> vacancyDtoToUpdate = vacancyRepository.findById(id);
        if (vacancyDtoToUpdate.isEmpty()) {
            throw new EntityNotFoundException("Vacancy not found");
        }
        return vacancyDtoToUpdate.get();
    }

    public Map<String, String> delete(Long id) throws EntityNotFoundException {
        Optional<Vacancy> vacancyToDelete = vacancyRepository.findById(id);
        if (vacancyToDelete.isEmpty()) {
            throw new EntityNotFoundException("Vacancy not found");
        }
        deleteVacancy(vacancyToDelete.get());
        return Map.of("message", "vacancy deleted",
                "status", HttpStatus.OK.toString());
    }

    private void deleteVacancy(Vacancy vacancyToDelete) {
        List<Long> candidatesToDelete = vacancyToDelete.getCandidates()
                .stream().filter(el -> !el.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId).toList();
        vacancyRepository.delete(vacancyToDelete);
        for (Long candidateId : candidatesToDelete) {
            candidateRepository.deleteById(candidateId);
        }
    }
}
