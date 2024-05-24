package faang.school.projectservice.validation.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.jpa.ScheduleRepository;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;

import static faang.school.projectservice.model.TaskStatus.DONE;

@Component
@RequiredArgsConstructor
public class InternshipValidator {
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final UserValidator userValidator;
    private final InternshipMapper internshipMapper;
    private final ScheduleRepository scheduleRepository;

    public void validateCreateInternship(long userId, Internship internship, InternshipDto internshipDto) {

        userValidator.validateUserExistence(userId);
        validateInternshipProperties(internship, internshipDto);
    }

    public void validateUpdateInternship(Internship internship, InternshipDto updatedInternshipDto) {
        Internship updatedInternship = internshipMapper.toEntity(updatedInternshipDto);
        validateInternshipNotExist(internship);
        validateInternshipNotStarted(internship);
        validateDates(updatedInternship.getStartDate(), updatedInternship.getEndDate());
        validateInternshipProperties(updatedInternship, updatedInternshipDto);
    }

    public void validateAddNewIntern(Internship internship, TeamMember newIntern) {
        validateInternshipNotStarted(internship);
//        validateInternshipProperties(internship);
        validateInternNotAlreadyInInternship(internship, newIntern);
    }

    public void validateFinishInternshipForIntern(Internship internship, TeamMember intern) {
        validateDeadline(internship);
        checkAllTasksDone(intern);
    }

    public void validateRemoveInternFromInternship(Internship internship, TeamMember intern) {
        validateInternshipContainsThisIntern(internship, intern);
    }

    public void validateTeamMemberDontHaveThisRole(TeamMember teamMember, TeamRole newRole) {
        if (teamMember.getRoles().contains(newRole)) {
            throw new DataValidationException(String.format("Team member %d already has role %s", teamMember.getId(), newRole.name()));
        }
    }

    private void validateInternshipNotExist(Internship internship) {
        if (internshipRepository.existsById(internship.getId())) {
            throw new DataValidationException("Internship already exists.");
        }
    }

    private void validateInternshipProperties(Internship internship, InternshipDto internshipDto) {
        validateProjectExistence(internship);
        validateInternsExistence(internship);
        validateStatusExistence(internshipDto);
        validateScheduleExistence(internship);
        validateInternshipDuration(internship);
        validateDates(internship.getStartDate(), internship.getEndDate());

    }

    private void validateStatusExistence(InternshipDto internshipDto) {

        boolean checkStatusExist = Arrays.stream(InternshipStatus.values())
                .anyMatch((status -> status.name().equals(internshipDto.getStatus())));

        if (!checkStatusExist) {
            throw new NotFoundException(String.format("Status %s not found", internshipDto.getStatus()));
        }
    }

    private void validateScheduleExistence(Internship internship) {
        if(scheduleRepository.findById(internship.getSchedule().getId()).isEmpty()){
            throw new NotFoundException(String.format("Schedule %d not found", internship.getSchedule().getId()));
        }
    }

    private void validateProjectExistence(Internship internship) {
        Project project = projectRepository.getProjectById(internship.getProject().getId());
        if (project == null) {
            throw new NotFoundException(String.format("Project %s not found", internship.getProject().getId()));
        }
    }

    private void validateInternsExistence(Internship internship) {
        if (internship.getInterns().isEmpty()) {
            throw new DataValidationException("Internship must have at least one intern.");
        }
    }

    private void validateInternshipDuration(Internship internship) {
        long monthsBetween = ChronoUnit.MONTHS.between(internship.getStartDate(), internship.getEndDate());
        if (monthsBetween > 3 || (monthsBetween == 3 && ChronoUnit.DAYS.between(internship.getStartDate().plusMonths(3), internship.getEndDate()) > 0)) {
            throw new DataValidationException("Internship duration exceeds three months.");
        }
    }

    private void validateMentorExistence(TeamMember mentor) {
        if (mentor == null || mentor.getUserId() == null) {
            throw new DataValidationException("Mentor is invalid.");
        }
        userValidator.validateUserExistence(mentor.getUserId());
    }

    private void validateInternNotAlreadyInInternship(Internship internship, TeamMember intern) {
        if (internship.getInterns().contains(intern)) {
            throw new DataValidationException("Intern with id: " + intern.getId() +
                                              " already is in internship with id: " + internship.getId());
        }
    }

    private void validateInternshipContainsThisIntern(Internship internship, TeamMember intern) {
        if (!internship.getInterns().contains(intern)) {
            throw new DataValidationException("Intern with id: " + intern.getId() +
                                              " is not in internship with id: " + internship.getId());
        }
    }

    private void validateInternshipNotStarted(Internship internship) {
        if (LocalDateTime.now().isAfter(internship.getStartDate())) {
            throw new DataValidationException("Internship start date has already begun.");
        }
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        if (!endDate.isAfter(startDate)) {
            throw new DataValidationException("Start date cannot be greater than or equal to end date.");
        }
    }

    private void validateDeadline(Internship internship) {
        if (!LocalDateTime.now().isBefore(internship.getEndDate())) {
            throw new DataValidationException(String.format("Internship %d already finished.", internship.getId()));
        }
    }

    private void checkAllTasksDone(TeamMember intern) {
        Optional<Task> notDoneTask = intern.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .filter(task -> !task.getStatus().equals(DONE))
                .findFirst();

        if (notDoneTask.isPresent()) {
            throw new DataValidationException("Intern with id: " + intern.getId() +
                                              " has unfinished task: " + notDoneTask.get().getName());
        }
    }
}
