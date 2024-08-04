package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectValidator {

    public void verifyProjectDoesNotHaveCalendar(Project project) {
        if (project.getCalendarId() != null) {
            String errMessage = String.format("Project with ID: %d already has calendar", project.getId());
            log.error(errMessage);
            throw new IllegalStateException(errMessage);
        }
    }
}