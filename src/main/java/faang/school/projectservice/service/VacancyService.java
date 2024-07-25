package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filters.filters.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@Slf4j
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final CandidateRepository candidateRepository;
    private final List<VacancyFilter> vacancyFilters;

    public VacancyDto getVacancy(Long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId).orElseThrow(() -> new EntityNotFoundException("Not found"));
        return vacancyMapper.toDto(vacancy);
    }

    public List<VacancyDto> getAll(VacancyFilterDto filterDto) {
        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();
        List<VacancyFilter> filters = vacancyFilters.stream().filter(el -> el.isValid(filterDto)).toList();
        for (VacancyFilter filter : filters) {
            vacancies = filter.apply(vacancies, filterDto);
        }
        return vacancies.map(vacancyMapper::toDto).toList();
    }

    public Map<String, String> create(VacancyDto vacancyDto) {
        TeamMember memberInCharge = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        if (memberInCharge == null) {
            throw new EntityNotFoundException("Team member not found");
        }
        isHrValidation(memberInCharge);
        Vacancy createdVacancy = vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));

        return Map.of("message", "vacancy created", "status", HttpStatus.CREATED.toString());
    }

    public Map<String, String> update(Long id, VacancyDto vacancyDto) {
        Vacancy editVacancy = getEditedVacancy(id);
        if (vacancyDto.getStatus().equals(VacancyStatus.CLOSED)
                && editVacancy.getCount() > editVacancy.getCandidates().size()) {
            log.info("Not enough candidates for vacancy " + editVacancy.getId());
            throw new DataValidationException("Not enough candidates");
        }
        attachCandidates(vacancyDto, editVacancy);
        vacancyRepository.save(vacancyMapper.toEntity(vacancyDto));
        return Map.of("message", "vacancy updated", "status", HttpStatus.OK.toString());
    }

    public Map<String, String> delete(Long id) {
        Vacancy vacancyToDelete = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacancy not found"));
        deleteVacancy(vacancyToDelete);
        return Map.of("message", "vacancy deleted", "status", HttpStatus.OK.toString());
    }

    private void attachCandidates(VacancyDto vacancyDto, Vacancy createdVacancy) {
        vacancyDto.getCandidateIds().forEach((candidateId) -> {
            Candidate candidateToAttach = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new EntityNotFoundException("Candidate not found"));
            candidateToAttach.setVacancy(createdVacancy);
            candidateRepository.save(candidateToAttach);
        });
    }

    private void isHrValidation(TeamMember memberInCharge) {
        boolean isHr = memberInCharge.getRoles().contains(TeamRole.OWNER) ||
                memberInCharge.getRoles().contains(TeamRole.MANAGER);
        if (!isHr) {
            throw new DataValidationException("User must be either manager or owner");
        }
    }

    private Vacancy getEditedVacancy(Long id) {
        return vacancyRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Vacancy not found"));
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
