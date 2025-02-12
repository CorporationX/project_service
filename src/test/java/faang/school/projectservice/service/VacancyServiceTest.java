package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyRequest;
import faang.school.projectservice.dto.vacancy.CreateVacancyResponse;
import faang.school.projectservice.dto.vacancy.UpdateVacancyRequest;
import faang.school.projectservice.dto.vacancy.UpdateVacancyResponse;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.VacancyValidationException;
import faang.school.projectservice.filter.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.validator.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private VacancyValidator vacancyValidator;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Captor
    private ArgumentCaptor<Vacancy> vacancyArgumentCaptor;

    @BeforeEach
    void init() {
        List<VacancyFilter> vacancyFilters = new ArrayList<>();
        vacancyFilters.add(mock(VacancyFilter.class));
        vacancyFilters.add(mock(VacancyFilter.class));

        vacancyService = new VacancyService(
                vacancyRepository,
                projectRepository,
                vacancyFilters,
                vacancyMapper,
                vacancyValidator);
    }

    @Test
    public void create_ShouldCreateVacancySuccessfully() {
        CreateVacancyRequest createRequest = CreateVacancyRequest.builder()
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .projectId(515L)
                .createdBy(123L)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L))
                .build();


        when(projectRepository.findById(createRequest.getProjectId()))
                .thenReturn(Optional.ofNullable(Project.builder().id(515L).build()));

        Vacancy createdVacancy = Vacancy.builder()
                .id(234L)
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .project(Project.builder().id(createRequest.getProjectId()).build())
                .createdBy(123L)
                .updatedBy(123L)
                .status(VacancyStatus.OPEN)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L))
                .build();

        when(vacancyRepository.save(vacancyArgumentCaptor.capture())).thenReturn(createdVacancy);

        final CreateVacancyResponse createResponse = vacancyService.create(createRequest);

        verify(vacancyMapper, times(1)).fromCreateRequest(createRequest);
        verify(vacancyValidator, times(1))
                .validateCreatingVacancy(vacancyArgumentCaptor.capture());
        verify(vacancyMapper, times(1)).toCreateResponse(createdVacancy);

        assertEquals("Backend-разработчик", createResponse.getName());
        assertEquals("Ищем в команду backend-разработчика на Java с опытом работы от 1 года",
                createResponse.getDescription());
        assertEquals(TeamRole.DEVELOPER, createResponse.getPosition());
        assertEquals(515L, createResponse.getProjectId());
        assertEquals(123L, createResponse.getCreatedBy());
        assertEquals(VacancyStatus.OPEN, createResponse.getStatus());
        assertEquals(150000.0, createResponse.getSalary());
        assertEquals(WorkSchedule.FULL_TIME, createResponse.getWorkSchedule());
        assertEquals(1, createResponse.getCount());
        assertArrayEquals(List.of(101L, 102L).toArray(), createResponse.getRequiredSkillIds().toArray(new Long[0]));
    }

    @Test
    public void update_ShouldUpdateVacancySuccessfully() {
        UpdateVacancyRequest updateRequest = UpdateVacancyRequest.builder()
                .id(234L)
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .projectId(515L)
                .updatedBy(123L)
                .status(VacancyStatus.OPEN)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L))
                .build();

        // vacancy должен быть изменяемым, поэтому builder не использую
        Vacancy vacancy = new Vacancy();
        vacancy.setId(234L);
        vacancy.setName("Backend-разработчик");
        vacancy.setDescription("Ищем в команду backend-разработчика на Java с опытом работы от 1 года");
        vacancy.setPosition(TeamRole.DEVELOPER);
        vacancy.setUpdatedBy(123L);
        vacancy.setStatus(VacancyStatus.OPEN);

        when(vacancyRepository.findById(updateRequest.getId()))
                .thenReturn(Optional.of(vacancy));

        Vacancy updatedVacancy = Vacancy.builder()
                .id(234L)
                .name("Backend-разработчик")
                .description("Ищем в команду backend-разработчика на Java с опытом работы от 1 года")
                .position(TeamRole.DEVELOPER)
                .project(Project.builder().id(515L).build())
                .updatedBy(123L)
                .status(VacancyStatus.OPEN)
                .salary(150000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(List.of(101L, 102L))
                .build();

        when(vacancyRepository.save(vacancyArgumentCaptor.capture())).thenReturn(updatedVacancy);

        final UpdateVacancyResponse updateResponse = vacancyService.update(updateRequest);

        verify(vacancyMapper, times(1)).update(updateRequest, vacancy);
        verify(vacancyValidator, times(1))
                .validateUpdatingVacancy(vacancyArgumentCaptor.capture());
        verify(vacancyMapper, times(1)).toUpdateResponse(updatedVacancy);

        assertEquals(234L, updatedVacancy.getId());
        assertEquals("Backend-разработчик", updateResponse.getName());
        assertEquals("Ищем в команду backend-разработчика на Java с опытом работы от 1 года",
                updateResponse.getDescription());
        assertEquals(TeamRole.DEVELOPER, updateResponse.getPosition());
        assertEquals(515L, updateResponse.getProjectId());
        assertEquals(123L, updateResponse.getUpdatedBy());
        assertEquals(123L, updateResponse.getUpdatedBy());
        assertEquals(VacancyStatus.OPEN, updateResponse.getStatus());
        assertEquals(150000.0, updateResponse.getSalary());
        assertEquals(WorkSchedule.FULL_TIME, updateResponse.getWorkSchedule());
        assertEquals(1, updateResponse.getCount());
        assertArrayEquals(List.of(101L, 102L).toArray(), updateResponse.getRequiredSkillIds().toArray(new Long[0]));
    }

    @Test
    public void delete_ShouldDeleteSuccessfully() {
        long id = 333L;
        when(vacancyRepository.findById(id))
                .thenReturn(Optional.of(new Vacancy()));
        vacancyService.delete(id);
        verify(vacancyRepository, times(1)).deleteById(id);
    }

    @Test
    public void delete_ShouldThrowVacancyExceptionWhenVacancyDoesNotExist() {
        long id = 333L;
        when(vacancyRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(VacancyValidationException.class, () -> vacancyService.delete(333L));
    }

    @Test
    public void getVacancyById_ShouldReturnSuccessfully() {
        long id = 333L;

        when(vacancyRepository.findById(id))
                .thenReturn(Optional.of(Vacancy.builder().candidates(new ArrayList<>()).build()));

        vacancyService.getById(id);

        verify(vacancyMapper, times(1)).toGetResponse(vacancyArgumentCaptor.capture());
    }

    @Test
    public void getVacancyById_ShouldThrowVacancyExceptionWhenDoesNotExist() {
        long id = 333L;

        when(vacancyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(VacancyValidationException.class, () -> vacancyService.getById(333L));
    }

    @Test
    public void getAll_ShouldReturnAllVacanciesVacanciesSuccessfully() {
        vacancyService.get(new VacancyFilterDto());

        verify(vacancyRepository, times(1)).findAll();
    }
}
