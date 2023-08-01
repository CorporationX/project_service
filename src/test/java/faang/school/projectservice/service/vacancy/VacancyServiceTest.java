package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.vacancy.VacancyValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @Mock
    private VacancyValidator validator;
    @Mock
    private VacancyMapper vacancyMapper;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private VacancyService service;

    VacancyDto vacancyDto = VacancyDto.builder().id(1L).projectId(1L).build();
    Project project = Project.builder().id(1L).build();
    Vacancy vacancy = Vacancy.builder().id(1L).build();

    @Test
    public void createVacancy_Test() {
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(projectRepository.getProjectById(vacancyDto.getProjectId())).thenReturn(project);
        Mockito.when(vacancyMapper.toEntity(vacancyDto)).thenReturn(vacancy);
        Mockito.when(vacancyRepository.save(vacancy)).thenReturn(vacancy);

        service.createVacancy(vacancyDto);
        Assertions.assertTrue(vacancy.getCreatedAt().isBefore(LocalDateTime.now()));

        Mockito.verify(vacancyMapper).toDto(vacancyRepository.save(vacancy));
    }
}
