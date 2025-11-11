package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.exception.InternshipAlreadyCompletedException;
import faang.school.projectservice.mapper.internship.InternshipDtoMapper;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipDtoMapper internshipDtoMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TaskRepository taskRepository;
    private final TeamMemberService teamMemberService;

    public InternshipDto createInternship(CreateInternshipDto createInternshipDto) {

        InternshipValidator.validateInternshipLengthDate(createInternshipDto);

        Project project = projectRepository.findByIdOrThrow(createInternshipDto.projectId());
        TeamMember mentor = teamMemberRepository.findMentorByIdOrThrow(createInternshipDto.mentorId());
        validateMentorBelongsToProject(project, mentor);

        List<TeamMember> interns = teamMemberRepository.findAllById(createInternshipDto.internsIds());
        validateInternsNotEmpty(interns);

        Internship internship = InternshipMapper.toInternship(
                createInternshipDto,
                project,
                mentor,
                interns);

        internship = internshipRepository.save(internship);

        return internshipDtoMapper.toInternshipDto(internship);
    }

    public InternshipDto updateInternship(long internshipId, UpdateInternshipDto updateIDto) {
        Internship internship = internshipRepository.findByIdOrThrow(internshipId);
        validateIfInternshipIsComplete(internship);

        if (updateIDto.status() == InternshipStatus.COMPLETED) {
            handleInternshipCompletion(internship);
            internship.setEndDate(LocalDateTime.now());
        } else {
            InternshipMapper.update(updateIDto, internship);
        }

        internship = internshipRepository.save(internship);
        return internshipDtoMapper.toInternshipDto(internship);
    }

    private void validateMentorBelongsToProject(Project project, TeamMember mentor) {
        if (project.getTeams() == null || project.getTeams().isEmpty()) {
            throw new EntityNotFoundException("Project has no associated teams.");
        }
        boolean belongsToAnyTeam = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().contains(mentor));
        if (!belongsToAnyTeam) {
            throw new EntityNotFoundException("Mentor is not a member of the project team");
        }
    }

    private void validateInternsNotEmpty(List<TeamMember> interns) {
        if (interns == null || interns.isEmpty()) {
            throw new EntityNotFoundException("Interns are missing.");
        }
    }

    private void validateIfInternshipIsComplete(Internship internship) {
        if (internship.getStatus() == InternshipStatus.COMPLETED) {
            throw new InternshipAlreadyCompletedException();
        }
    }

    private void handleInternshipCompletion(Internship internship) {
        List<TeamMember> interns = internship.getInterns();
        for (TeamMember intern : interns) {
            List<Task> tasks = taskRepository.findByPerformerUserId(intern.getId());

            if (!areAllTasksCompleted(tasks)) {
                //������� �� �������;
                teamMemberService.removeMemberFromTeam(intern);
            }
        }
        for (TeamMember intern : interns) {
            intern.setRoles(Collections.singletonList(internship.getRole()));
        }
    }

    private boolean areAllTasksCompleted(List<Task> tasks) {
        return tasks.stream().allMatch(task -> task.getStatus() == TaskStatus.DONE);
    }
}