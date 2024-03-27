package faang.school.projectservice.validation.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorMomentTest {

    private MomentValidator momentValidator;
    private MomentDto momentDto;
    private Project project;
    private Project project1;
    private Moment moment;

    @BeforeEach
    void init() {
        momentDto = MomentDto.builder()
                .name("test")
                .build();
        moment = Moment.builder()
                .projects(Arrays.asList(project, project1))
                .build();
        project = Project.builder()
                .status(ProjectStatus.CANCELLED)
                .build();
        project1 = Project.builder()
                .status(ProjectStatus.CANCELLED)
                .build();
        momentValidator = new MomentValidator();
    }

    @Test
    public void shouldPassValidationWhenDtoHasValidName() {
        momentDto.setName("Valid Name");
        momentValidator.validatorMomentName(momentDto);
    }

    @Test
    public void testValidatorMomentNameIsBlank() {
        momentDto.setName(" ");
        assertThrows(DataValidationException.class, () -> momentValidator.validatorMomentName(momentDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectIdsNull() {
        momentDto.setProjectIds(null);

        assertThrows(IllegalArgumentException.class, () -> momentValidator.validatorProjectOfMoment(momentDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectIdsEmpty() {
        momentDto.setProjectIds(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> momentValidator.validatorProjectOfMoment(momentDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectIdNull() {
        List<Long> projectIds = new ArrayList<>();
        projectIds.add(1L);
        projectIds.add(null);
        momentDto.setProjectIds(projectIds);

        assertThrows(IllegalArgumentException.class, () -> momentValidator.validatorProjectOfMoment(momentDto));
    }

    @Test
    public void shouldPassValidationWhenProjectIdsValid() {
        List<Long> projectIds = new ArrayList<>();
        projectIds.add(1L);
        projectIds.add(2L);
        momentDto.setProjectIds(projectIds);

        momentValidator.validatorProjectOfMoment(momentDto);
    }
}
