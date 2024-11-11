package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateRepository candidateRepository;

    private final List<Filter<VacancyFilterDto, Vacancy>> vacancyFilters;

    public VacancyDto createVacancy(CreateVacancyDto createVacancyDto) {
        Vacancy vacancy = vacancyMapper.toEntity(createVacancyDto);

        TeamMember curator = teamMemberRepository.findById(createVacancyDto.getCreatedBy())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Curator not found by id: %s", createVacancyDto.getCreatedBy())));

        if (!curator.getRoles().contains(TeamRole.OWNER)) {
            throw new IllegalArgumentException("Curator " + curator.getId() + " does not have the required OWNER role");
        }

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy created with id: {}", savedVacancy.getId());

        return vacancyMapper.toVacancyDto(savedVacancy);
    }

    public VacancyDto updateVacancy(Long id, VacancyDto vacancyDto) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy not found by id: %s", id)));
    
        if (vacancyDto.getStatus() == VacancyStatus.CLOSED) {
            if (vacancy.getCandidates().size() < vacancy.getCount()) {
                throw new IllegalStateException("Cannot close vacancy " + vacancy.getId() + ", not enough candidates selected");
            }
    
            vacancy.getCandidates().forEach(candidate -> {
                TeamMember teamMember = teamMemberRepository.findById(candidate.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", id)));
    
                if (!teamMember.getTeam().getProject().getId().equals(vacancy.getProject().getId())) {
                    throw new IllegalStateException("Candidate " + candidate.getId() + " is not a member of the project " + vacancy.getProject().getId());
                }
    
                teamMember.getRoles().add(TeamRole.DEVELOPER);
                teamMemberRepository.save(teamMember);
            });
        }
    
        Project project = projectRepository.findById(vacancy.getProject().getId())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Project not found by id: %s", vacancy.getProject().getId())));
        vacancy.setProject(project);
    
        Vacancy updatedVacancy = vacancyRepository.save(vacancy);
        log.info("Vacancy updated with id: {}", updatedVacancy.getId());

        return vacancyMapper.toVacancyDto(updatedVacancy);
    }
    
    @Transactional
    public void deleteVacancy(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy not found by id: %s", id)));
        
        List<Long> candidateIdsToDelete = vacancy.getCandidates().stream()
                .filter((candidate) -> !candidate.getCandidateStatus().equals(CandidateStatus.ACCEPTED))
                .map(Candidate::getId)
                .toList();
                
        candidateRepository.deleteAllById(candidateIdsToDelete);
    
        vacancyRepository.deleteById(id);
        log.info("Vacancy deleted with id: {}", id);
    }
    

    public List<VacancyDto> getAllVacancies(VacancyFilterDto filterDto) {
        if (filterDto == null) {
            throw new DataValidationException("Filter can't be null!");
        }

        Stream<Vacancy> vacancies = vacancyRepository.findAll().stream();

        return vacancyFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(
                    vacancies, 
                    (stream, filter) -> filter.applyFilter(stream, filterDto), 
                    (v1, v2) -> v1
                )
                .map(vacancyMapper::toVacancyDto)
                .toList();
    }

    public VacancyDto getVacancyById(Long id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Vacancy not found by id: %s", id)));
        return vacancyMapper.toVacancyDto(vacancy);
    }
}
