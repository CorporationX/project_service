package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static faang.school.projectservice.model.TaskStatus.DONE;

@Component
@RequiredArgsConstructor
public class InternshipValidator {

    private final InternshipRepository internshipRepository;

    @Transactional
    public void validateInternshipExistence(InternshipDto internshipDto) {
        boolean isAlreadyExists = internshipRepository.existsById(internshipDto.getId());
        if (isAlreadyExists) {
            throw new DataValidationException("Internship already exists");
        }
    }

    public void validateInternshipNotStarted(Internship internship) {
        if (LocalDateTime.now().isAfter(internship.getStartDate())) {
            throw new DataValidationException("Internship start date has already begun");
        }
    }

    public void validateInternshipNotCompleted(Internship internship) {
        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot update a completed internship.");
        }
    }

    public void validateDateNotExpired(Internship internship) {
        if (LocalDateTime.now().isBefore(internship.getEndDate()))
            throw new DataValidationException("The internship is not over");
    }

    public void validateUpdatedInternshipDiffersByLast(Internship internship, Internship updatedInternship) {
        if (internship.equals(updatedInternship)) {
            throw new DataValidationException("No changes detected in the provided update information");
        }
    }

    public void checkAllTasksDone(TeamMember intern) {
        Optional<Task> notDoneTask = intern.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .filter(task -> !task.getStatus().equals(DONE))
                .findFirst();

        if (notDoneTask.isPresent()) {
            throw new DataValidationException("Intern with id: " + intern.getId() +
                    " not finished task " + notDoneTask.get().getName());
        }
    }

    public void validateInternNotAlreadyInInternship(Internship internship, TeamMember intern) {
        if (internship.getInterns().contains(intern)) {
            throw new DataValidationException("Intern with id: " + intern.getId() +
                    " already is in internship with id: " + internship.getId());
        }
    }

    public void validateInternshipContainsThisIntern(Internship internship, TeamMember intern) {
        if (!internship.getInterns().contains(intern)) {
            throw new DataValidationException("an intern with that id: " + intern.getId() +
                    " is not in an internship with an id " + internship.getId());
        }
    }
}
