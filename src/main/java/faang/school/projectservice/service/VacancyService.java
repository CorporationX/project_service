package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.VacancyCreateDto;
import faang.school.projectservice.dto.client.VacancyFilterDto;
import faang.school.projectservice.dto.client.VacancyResponseDto;
import faang.school.projectservice.dto.client.VacancyUpdateDto;
import faang.school.projectservice.mapper.VacancyCreateMapper;
import faang.school.projectservice.mapper.VacancyResponseMapper;
import faang.school.projectservice.mapper.VacancyUpdateMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.filter.VacancyFilter;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.projectservice.model.CandidateStatus.ACCEPTED;


@Service
@AllArgsConstructor
@Slf4j
public class VacancyService {
    private final VacancyRepository vacancyRepository;
    private final VacancyCreateMapper vacancyCreateMapper;
    private final VacancyUpdateMapper vacancyUpdateMapper;
    private final VacancyResponseMapper vacancyResponseMapper;
    private final List<VacancyFilter> listVacancyFilters;
    private final TeamMemberRepository teamMemberRepository;


    public Vacancy createVacancy(VacancyCreateDto vacancyCreateDto) {
        log.info("Start of vacancy creation: {}", vacancyCreateDto.getName());
        if (createVacancyValidation(vacancyCreateDto)) {
            Vacancy vacancy = vacancyRepository.save(vacancyCreateMapper.toVacancy(vacancyCreateDto));
            log.info("Vacancy created.");
            return vacancy;
        } else {
            log.error("Vacancy not created, incorrect data entered.");
            throw new IllegalArgumentException("Vacancy not created, incorrect data entered.");
        }
    }

    public VacancyResponseDto updateVacancy(Long id, VacancyUpdateDto vacancyUpdateDto) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %d", id)));

        if (vacancy.getStatus().equals("CLOSED")) {
            throw new IllegalArgumentException("Vacancy with a closed status cannot be updated.");
        }
        long acceptedCandidates = vacancy.getCandidates()
                .stream()
                .filter(candidateDto -> candidateDto.getCandidateStatus().equals(ACCEPTED))
                .count();

        if (vacancy.getCount().equals(acceptedCandidates)) {
            vacancy.setStatus(VacancyStatus.valueOf("CLOSED"));
        }
        Vacancy updatedVacancy = vacancyUpdateMapper.toVacancy(vacancyUpdateDto);
        vacancy.setName(updatedVacancy.getName());
        vacancy.setDescription(updatedVacancy.getDescription());
        vacancy.setSalary(updatedVacancy.getSalary());
        vacancy.setStatus(updatedVacancy.getStatus());
        vacancy.setWorkSchedule(updatedVacancy.getWorkSchedule());
        vacancy.setRequiredSkillIds(updatedVacancy.getRequiredSkillIds());
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        if(savedVacancy != null){
            log.info("Vacancy updated: {}", id);
        }
        return vacancyResponseMapper.toVacancyDto(savedVacancy);
    }

    public void deleteVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %d", id)));
        vacancy.getCandidates().removeIf(candidate -> {
            if (!candidate.getCandidateStatus().equals(ACCEPTED)) {
                log.info("Removing candidate: {}", candidate);
                return true;
            } else {
                log.info("Candidate has not been removed: {}", candidate);
                return false;
            }
        });
        vacancyRepository.deleteById(id);
        log.info("Successfully deleted vacancy with id: {}", id);
    }

    public List<Vacancy> findVacancyByFilter(VacancyFilterDto vacancyFilterDto) {
        Stream<Vacancy> allVacancy = vacancyRepository.findAll().stream();
        List<Vacancy> vacancyList = listVacancyFilters.stream().filter(filter -> filter.isApplicable(vacancyFilterDto))
                .flatMap(filter -> filter.apply(allVacancy, vacancyFilterDto)).collect(Collectors.toList());
        log.info("Found {} vacancies for project name: {}", vacancyList.size());
        return vacancyList;
    }

    public Vacancy getVacancyById(Long id) {
        log.debug("Request for a vacancy with ID: {}", id);
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy doesn't exist by id: %d", id)));
        log.debug("Successfully retrieved vacancy with ID: {}", id);
        return vacancy;
    }

    private boolean createVacancyValidation(VacancyCreateDto vacancyCreateDto) {
        try {
            if (vacancyCreateDto.getProjectId() == null) {
                throw new IllegalArgumentException("The vacancy must be related to the project.");
            }
            if (vacancyCreateDto.getCreatedBy() == null) {
                throw new IllegalArgumentException("The vacancy must have a supervisor");
            }
            TeamMember teamMember = teamMemberRepository.findById(vacancyCreateDto.getCreatedBy());
            if (!teamMember.getRoles().contains(TeamRole.MANAGER) && !teamMember.getRoles().contains(TeamRole.OWNER)) {
                throw new IllegalArgumentException("The vacancy supervisor can be a team member with the MANAGER or OWNER role");
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
