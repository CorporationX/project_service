package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final TeamService teamService;
    private final VacancyMapper vacancyMapper;
    private final ValidationTeamMember validationTeamMember;
    private final ProjectService projectService;

    public VacancyDto createVacancy(Long creatorId, VacancyDto vacancyDto) {
        validationTeamMember.checkRoleIsOwnerOrManager(teamService.findMemberById(creatorId));
        Vacancy vacancyEntity = vacancyMapper.toEntity(vacancyDto);

        vacancyEntity.setCreatedBy(creatorId);
        vacancyEntity.setCandidates(new ArrayList<>());
        vacancyEntity.setProject(projectService.getProjectById(vacancyDto.getProjectId()));

        return vacancyMapper.toDto(vacancyRepository.save(vacancyEntity));
    }
}
