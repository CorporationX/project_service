package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VacancyValidator {

    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;
    private final TeamMemberRepository teamMemberRepository;
    @Autowired
    public VacancyValidator(ProjectRepository projectRepository, VacancyRepository vacancyRepository, TeamMemberRepository teamMemberRepository) {
        this.projectRepository = projectRepository;
        this.vacancyRepository = vacancyRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public void createVacancyValidator(VacancyDto vacancyDto) {
        if(projectRepository.existsById(vacancyDto.getProjectId())) {
            throw new RuntimeException("This project does not exist!");
        }
        checkCreatorInTeam(vacancyDto.getProjectId(), vacancyDto.getCreatedBy());
        checkCreatorRole(vacancyDto.getCreatedBy());

    }

    public Vacancy getVacancyValidator(Long vacancyId) {
        Optional<Vacancy> vacancy = vacancyRepository.findById(vacancyId);
        if(vacancy.isEmpty()) {
            throw new RuntimeException("Vacancy not found!");
        }
        return vacancy.get();
    }

    private void checkCreatorInTeam(Long projectId, Long creatorId) {
        Project project = projectRepository.getProjectById(projectId);
        List<Team> teamsOnProject = project.getTeams();
        List<Long> teamsMembersIds = teamsOnProject.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .toList();

        if (!teamsMembersIds.contains(creatorId)) {
            throw new RuntimeException("The user does not have enough rights");
        }
    }

   private void checkCreatorRole(Long creatorId) {
        TeamMember teamMember = teamMemberRepository.findById(creatorId);
        List<TeamRole> teamRoles = teamMember.getRoles();
        if (!(teamRoles.contains(TeamRole.OWNER)) || !(teamRoles.contains(TeamRole.MANAGER))) {
            throw new RuntimeException("The role does not allow to create a vacancy");
        }
   }


}
