package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class Validator {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectMapper projectMapper;

    public void createValidation(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Ошибка: пользователь id" + projectDto.getOwnerId() +
                    " уже создал проект с названием " + projectDto.getName());
        }
    }

    public void userValidator(Long userId) {
        if (!projectRepository.findAll().stream()
                .anyMatch(project -> project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(teamMember -> teamMember.getUserId().equals(userId))))) {
            throw new DataValidationException("Ошибка: пользователь id" + userId + "не найден");
        }
    }
}
