package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.OpenVacancyRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private OpenVacancyRequestValidator openVacancyRequestValidator;
    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @InjectMocks
    private VacancyService vacancyService;

    @Captor
    private ArgumentCaptor<Vacancy> vacancyCaptor;

    @Test
    public void testOpenVacancy_FailedProjectValidation_Throws() {
        var requestDto = createOpenVacancyRequestDto(0, 1, null);
        when(openVacancyRequestValidator.validateProject(requestDto))
                .thenThrow(new DataValidationException("Invalid project"));

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.openVacancy(requestDto),
                "Invalid project");
    }

    @Test
    public void testOpenVacancy_FailedAuthorValidation_Throws() {
        var requestDto = createOpenVacancyRequestDto(1, 0, null);
        when(openVacancyRequestValidator.validateProject(requestDto)).thenReturn(new Project());
        when(openVacancyRequestValidator.validateAuthor(requestDto))
                .thenThrow(new DataValidationException("Invalid author"));

        assertThrowsExactly(
                DataValidationException.class,
                () -> vacancyService.openVacancy(requestDto),
                "Invalid author");
    }

    @Test
    public void testOpenVacancy_FailedSalaryValidation_Throws() {
        // Arrange
        var projectId = 1L;
        var authorId = 2L;
        var requestDto = createOpenVacancyRequestDto(projectId, authorId, 10.5);

        var project = Project.builder()
                .id(projectId)
                .name("Test project")
                .build();
        when(openVacancyRequestValidator.validateProject(requestDto)).thenReturn(project);

        var author = TeamMember.builder()
                .id(authorId)
                .nickname("Test author")
                .build();
        when(openVacancyRequestValidator.validateAuthor(requestDto)).thenReturn(author);

        // Act
        vacancyService.openVacancy(requestDto);

        // Assert
        verify(vacancyRepository, times(1)).save(vacancyCaptor.capture());
        var savedVacancy = vacancyCaptor.getValue();
        assertEquals(VacancyStatus.OPEN, savedVacancy.getStatus());
        assertEquals(authorId, savedVacancy.getCreatedBy());
        assertEquals(projectId, savedVacancy.getProject().getId());
        assertEquals(project.getName(), savedVacancy.getProject().getName());
    }

    private static OpenVacancyRequestDto createOpenVacancyRequestDto(long projectId, long authorId, Double salary) {
        return new OpenVacancyRequestDto(
                "Test name",
                "Test description",
                projectId,
                TeamRole.ANALYST,
                1,
                authorId,
                salary,
                null,
                null);
    }
}