package faang.school.projectservice.validator.internship;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.TaskStatus.DONE;

@Component
@RequiredArgsConstructor
public class InternshipValidator {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void validateInternshipCreation(Internship internship) {

        validateInternshipNotStarted(internship);
        validateInternshipHasProject(internship);
        validateInternshipHasAnyIntern(internship);
        validateInternshipLastsNoLongerThan3Months(internship);
        validateInternshipHasMentor(internship);
    }

    private void validateInternshipHasMentor(Internship internship) {
        TeamMember mentor = teamMemberRepository.findById(internship.getMentorId());
        mentor.
    }

    @Transactional
    public void validateInternshipUpdate(Internship internship, Internship updatedInternship) {

        validateInternshipNotStarted(internship);
        validateInternshipHasProject(updatedInternship);
        validateInternshipHasAnyIntern(updatedInternship);
        validateInternshipNotCompleted(internship);
        validateUpdatedInternshipDiffersByLast(internship, updatedInternship);
    }

    @Transactional
    public void validateAddingNewIntern(Internship internship, TeamMember newIntern) {

        validateInternshipNotStarted(internship);
        validateInternshipHasProject(internship);
        validateInternNotAlreadyInInternship(internship, newIntern);
    }

    @Transactional
    public void validateFinishingInternshipForIntern(Internship internship, TeamMember intern) {

        validateDateNotExpired(internship);
        checkAllTasksDone(intern);
    }

    @Transactional
    public void validateRemovingInternFromInternship(Internship internship, TeamMember intern) {

        validateInternshipContainsThisIntern(internship, intern);
    }

    @Transactional
    public void validateInternshipExistence(InternshipDto internshipDto) {
        boolean isAlreadyExists = internshipRepository.existsById(internshipDto.getId());
        if (isAlreadyExists) {
            throw new DataValidationException("Internship already exists");
        }
    }

    @Transactional
    public void validateInternshipHasProject(Internship updatedInternship) {

        Project project = projectRepository.getProjectById(updatedInternship.getId());
        List<Internship> projectInternships = project.getInternships();

        boolean containsInternship = projectInternships.stream()
                .anyMatch(internship -> internship.getId().equals(updatedInternship.getId()));

        if (!containsInternship) {
            throw new NotFoundException("Not found internship with id: " + updatedInternship.getId() + " in project with id: " + project.getId());
        }
    }

    public void validateInternshipHasAnyIntern(Internship internship) {

        if(internship.getInterns().isEmpty()){
            throw new DataValidationException("Internship cannot be empty");
        }
    }

    public void validateInternshipLastsNoLongerThan3Months(Internship internship) {
        long monthsBetween = ChronoUnit.MONTHS.between(internship.getStartDate(), internship.getEndDate());

        if (monthsBetween > 3) {
            throw new DataValidationException("Internship with id: " + internship.getId() + " lasts more than three months");
        } else if (monthsBetween == 3) {

            long daysInThreeMonths = ChronoUnit.DAYS.between(internship.getStartDate().plusMonths(3), internship.getEndDate());

            if (daysInThreeMonths > 0) {
                throw new DataValidationException("Internship with id: " + internship.getId() + " lasts more than three months");
            }
        }
    }

    private void validateInternshipNotStarted(Internship internship) {
        if (LocalDateTime.now().isAfter(internship.getStartDate())) {
            throw new DataValidationException("Internship start date has already begun");
        }
    }

    private void validateInternshipNotCompleted(Internship internship) {
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot update a completed internship.");
        }
    }

    private void validateDateNotExpired(Internship internship) {
        if (LocalDateTime.now().isBefore(internship.getEndDate()))
            throw new DataValidationException("The internship is not over");
    }

    private void validateUpdatedInternshipDiffersByLast(Internship internship, Internship updatedInternship) {
        if (internship.equals(updatedInternship)) {
            throw new DataValidationException("No changes detected in the provided update information");
        }
    }

    private void checkAllTasksDone(TeamMember intern) {
        Optional<Task> notDoneTask = intern.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .filter(task -> !task.getStatus().equals(DONE))
                .findFirst();

        if (notDoneTask.isPresent()) {
            throw new DataValidationException("Intern with id: " + intern.getId() +
                                              " not finished task " + notDoneTask.get().getName());
        }
    }

    private void validateInternNotAlreadyInInternship(Internship internship, TeamMember intern) {
        if (internship.getInterns().contains(intern)) {
            throw new DataValidationException("Intern with id: " + intern.getId() +
                                              " already is in internship with id: " + internship.getId());
        }
    }

    private void validateInternshipContainsThisIntern(Internship internship, TeamMember intern) {
        if (!internship.getInterns().contains(intern)) {
            throw new DataValidationException("an intern with that id: " + intern.getId() +
                                              " is not in an internship with an id " + internship.getId());
        }
    }
}
