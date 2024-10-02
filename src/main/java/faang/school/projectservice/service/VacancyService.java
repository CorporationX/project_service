package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.projectservice.model.CandidateStatus.ACCEPTED;


@Service
@AllArgsConstructor
public class VacancyService {
    private static final Logger logger = LoggerFactory.getLogger(VacancyService.class);
    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public VacancyDto createVacancy(VacancyDto vacancyDto) {
        logger.info("Start of vacancy creation: {}", vacancyDto.getName());
        createVacancyValidation(vacancyDto);
        vacancyRepository.save(vacancyMapper.vacancyDtoToVacancy(vacancyDto));
        logger.info("Vacancy created with ID: {}", vacancyDto.getId());
        return vacancyDto;
    }

    public void updateVacancy(VacancyDto vacancyDto) {
        logger.info("Start updating vacancy with ID: {}", vacancyDto.getId());
        Long acceptedCandidates = vacancyDto.getCandidates()
                .stream()
                .filter(candidateDto -> candidateDto.getCandidateStatus().equals(ACCEPTED))
                .count();
        if (vacancyDto.getCount().equals(acceptedCandidates)) {
            vacancyRepository.findById(vacancyDto.getId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %s", vacancyDto.getId())))
                    .setStatus(VacancyStatus.valueOf("CLOSED"));
        }
        vacancyRepository.flush();
        logger.info("Vacancy updated: {}", vacancyDto.getId());
    }

    public void deleteVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %s", id)));
        vacancy.getCandidates().removeIf(candidate -> {
            boolean shouldRemove = !candidate.getCandidateStatus().equals(ACCEPTED);
            if (shouldRemove) {
                logger.info("Removing candidate: {}", candidate);
            }
            logger.info("Candidate has not been removed: {}", candidate);
            return shouldRemove;
        });
        vacancyRepository.deleteById(id);
        logger.info("Successfully deleted vacancy with id: {}", id);
    }

    public List<Vacancy> findVacancyByName(String name) {
        List<Vacancy> vacancyList = vacancyRepository.findVacanciesByProjectName(name);
        logger.info("Found {} vacancies for project name: {}", vacancyList.size(), name);
        return vacancyList;
    }

    public Vacancy getVacancyById(Long id) {
        logger.debug("Request for a vacancy with ID: {}", id);
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Vacancy doesn't exist by id: %s", id);
                    logger.error(errorMessage);
                    return new EntityNotFoundException(errorMessage);
                });
        logger.info("Successfully retrieved vacancy with ID: {}", id);
        return vacancy;
    }

    private void createVacancyValidation(VacancyDto vacancyDto) {
        Long id = vacancyDto.getId();
        projectRepository.existsById(vacancyDto.getProjectId());
        if (vacancyDto.getProjectId() == null) {
            throw new IllegalArgumentException(String.format("The vacancy must be related to the project", id));
        }
        if (vacancyDto.getCreatedBy() == null) {
            throw new IllegalArgumentException(String.format("The vacancy must have a supervisor", id));
        }
        TeamMember teamMember = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        if (!teamMember.getRoles().contains("MANAGER") || !teamMember.getRoles().contains("OWNER")) {
            throw new IllegalArgumentException(String
                    .format("The vacancy supervisor can be a team member with the MANAGER or OWNER role", id));
        }
    }

}
