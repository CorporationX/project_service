package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectValidatorTest {

    private String calendarId;
    private Project project;
    private ProjectValidator projectValidator;

    @BeforeEach
    public void setUp(){
        calendarId = "calendarId";
        project = new Project();
        projectValidator = new ProjectValidator();
    }

    @Test
    @DisplayName("testing verifyProjectDoesNotHaveCalendar method with non-appropriate value")
    void testVerifyProjectDoesNotHaveCalendarWithNonAppropriateValue() {
        project.setCalendarId(calendarId);
        assertThrows(IllegalStateException.class, () -> projectValidator.verifyProjectDoesNotHaveCalendar(project));
    }

    @Test
    @DisplayName("testing verifyProjectDoesNotHaveCalendar method with appropriate value")
    void testVerifyProjectDoesNotHaveCalendarWithAppropriateValue() {
        assertDoesNotThrow(() -> projectValidator.verifyProjectDoesNotHaveCalendar(project));
    }
}