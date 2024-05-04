package faang.school.projectservice.service.validator;

import faang.school.projectservice.dto.Vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Data
@Component
public class VacancyValidator {
    private TeamMemberRepository teamMemberRepository;

    public void canCreateOrThrowException(VacancyDto vacancyDto) {
        TeamMember curator = teamMemberRepository.findById(vacancyDto.getCreatedBy());
        List<TeamRole> canCreateVacancy = List.of(TeamRole.OWNER,
                TeamRole.MANAGER);
        if (null == curator ||
                Collections.disjoint(canCreateVacancy, curator.getRoles())) {
            throw new DataValidationException("This user can not create vacancy!");
        }
    }

    public void checkProjectStatus(Project project) {
        if (project == null || project.getStatus().equals(ProjectStatus.CANCELLED) ||
                project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException("This project can not have vacancies!");
        }
    }
}
