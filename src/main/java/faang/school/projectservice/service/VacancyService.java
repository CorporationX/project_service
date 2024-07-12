package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.UpdateVacancyDto;
import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exceptions.EntityNotFoundException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final VacancyMapper vacancyMapper;
    private final CandidateRepository candidateRepository;

    public Map<String, String> create(VacancyDto vacancyDto) throws ValidationException {
        TeamMember memberInCharge = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        if (memberInCharge == null) {
            throw new EntityNotFoundException("Team member not found");
        }
        boolean isHr = memberInCharge.getRoles().contains(TeamRole.OWNER) || memberInCharge.getRoles().contains(TeamRole.MANAGER);
        if (!isHr) {
            throw new ValidationException("User must be either manager or owner");
        }
        Vacancy createdVacancy = vacancyRepository
                .create(vacancyDto.getName(), vacancyDto.getDescription(), vacancyDto.getProjectId(),
                        vacancyDto.getCreatedBy(), vacancyDto.getUpdatedBy(), vacancyDto.getStatus().toString(),
                        vacancyDto.getSalary(), vacancyDto.getWorkSchedule().toString(), vacancyDto.getCount());
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
        return Map.of("message", "vacancy created",
                "status", HttpStatus.CREATED.toString());
    }

    public Map<String, String> update(Long id, VacancyDto vacancyDto) throws ValidationException {
        Optional<Vacancy> vacancyDtoToUpdate = vacancyRepository.findById(id);
        if (vacancyDtoToUpdate.isEmpty()) {
            throw new EntityNotFoundException("Vacancy not found");
        }
        Vacancy editVacancy = vacancyDtoToUpdate.get();
        System.out.println("cands " + editVacancy.getCandidates());
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

    public Map<String, String> delete(Long id) throws EntityNotFoundException {
        Optional<Vacancy> vacancyToDelete = vacancyRepository.findById(id);
        if (vacancyToDelete.isEmpty()) {
            throw new EntityNotFoundException("Vacancy not found");
        }
        Vacancy vacancyToDeleteEntity = vacancyToDelete.get();
        List<Long> candidatesToDelete = vacancyToDeleteEntity.getCandidates()
                .stream().filter(el -> !el.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId).toList();
        vacancyRepository.delete(vacancyToDeleteEntity);
        for (Long candidateId : candidatesToDelete) {
            candidateRepository.deleteById(candidateId);
        }
        return Map.of("message", "vacancy deleted",
                "status", HttpStatus.OK.toString());
    }
}
