package faang.school.projectservice.validator.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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
        momentValidator.MomentValidatorName(momentDto);
    }

    @Test
    public void TestValidatorMomentNameIsBlank() {
        momentDto.setName(" ");
        assertThrows(EntityNotFoundException.class, () -> momentValidator.MomentValidatorName(momentDto));
    }

    @Test
    public void TestValidatorMomentNameIsNull() {
        momentDto.setName(null);
        assertThrows(EntityNotFoundException.class, () -> momentValidator.MomentValidatorName(momentDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectIdsNull() {
        momentDto.setProjectIds(null);

        assertThrows(IllegalArgumentException.class, () -> momentValidator.MomentValidatorProject(momentDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectIdsEmpty() {
        momentDto.setProjectIds(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> momentValidator.MomentValidatorProject(momentDto));
    }

    @Test
    public void shouldThrowExceptionWhenProjectIdNull() {
        List<Long> projectIds = new ArrayList<>();
        projectIds.add(1L);
        projectIds.add(null);
        momentDto.setProjectIds(projectIds);

        assertThrows(IllegalArgumentException.class, () -> momentValidator.MomentValidatorProject(momentDto));
    }

    @Test
    public void shouldPassValidationWhenProjectIdsValid() {
        List<Long> projectIds = new ArrayList<>();
        projectIds.add(1L);
        projectIds.add(2L);
        momentDto.setProjectIds(projectIds);

        momentValidator.MomentValidatorProject(momentDto);
    }
}
