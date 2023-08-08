package faang.school.projectservice.service;

import faang.school.projectservice.dto.Vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.Vacancy.ExtendedVacancyDto;
import faang.school.projectservice.dto.Vacancy.UpdateVacancyDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;


    @Transactional
    public ExtendedVacancyDto create(CreateVacancyDto vacancyDto) {
        validateExistProjectInSystem(vacancyDto);
        validateStatusOfVacancyCreator(vacancyDto);
        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setStatus(VacancyStatus.OPEN);
        vacancy.setCandidates(new ArrayList<>());
        return saveEntity(vacancy);
    }

    @Transactional
    public ExtendedVacancyDto update(UpdateVacancyDto vacancyDto) {
        if (vacancyDto.getStatus().equals(VacancyStatus.CLOSED)) {
            validateAvailableCloseStatus(vacancyDto);
        }
        return vacancyRepository.findById(vacancyDto.getId())
                .map(vacancy -> {
                    vacancyMapper.updateFromDto(vacancyDto, vacancy);
                    return saveEntity(vacancy);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Vacancy with id %d does not exist.", vacancyDto.getId()))
                );
    }

    private ExtendedVacancyDto saveEntity(Vacancy vacancy) {
        vacancy = vacancyRepository.save(vacancy);
        return vacancyMapper.toDto(vacancy);
    }

    private void validateExistProjectInSystem(CreateVacancyDto vacancyDto) {
        if (!projectRepository.existsById(vacancyDto.getProjectId())) {
            throw new EntityNotFoundException(String.format(
                    "Project with id %d does not exist.", vacancyDto.getProjectId())
            );
        }
    }

    private void validateStatusOfVacancyCreator(CreateVacancyDto vacancyDto) {
        Long ownerProjectId = projectRepository.getProjectById(vacancyDto.getProjectId()).getOwnerId();
        if (vacancyDto.getCreatedBy() != ownerProjectId) {
            throw new DataValidException(String.format(
                    "Status in project %s should be \"OWNER\" for create vacancy.", vacancyDto.getProjectId())
            );
        }
    }

    private void validateAvailableCloseStatus(UpdateVacancyDto vacancyDto) {
//        считаем кандидатов с нужным статусом в списке кандидатов
        if (vacancyDto.getCount() < getCountAcceptedCandidate(vacancyDto)) {
            throw new DataValidException("Count of accepted candidate less than required");
        }
    }

    private int getCountAcceptedCandidate(UpdateVacancyDto vacancyDto) {
        return candidateRepository.countAllByVacancyIdAndStatusAndId(
                vacancyDto.getId(),
                CandidateStatus.ACCEPTED,
                vacancyDto.getCandidates());

    }

}
