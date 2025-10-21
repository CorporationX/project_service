package faang.school.project_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.SearchVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.VacancyValidationException;
import faang.school.projectservice.filter.VacancyFilter;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.VacancyServiceImpl;
import faang.school.projectservice.validation.vacancy.VacancyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    private final static long DEFAULT_PROJECT_ID = 1L;
    private final static int DEFAULT_VACANCY_COUNT = 5;
    private final static long DEFAULT_USER_ID = 1L;
    private final static long DEFAULT_VACANCY_ID = 1L;
    private static final long DEFAULT_CORRECT_CONTEXT_USER_ID = 1L;

    private final long userId = DEFAULT_USER_ID;
    private final long projectId = DEFAULT_PROJECT_ID;
    private final long correctContextUserId = DEFAULT_CORRECT_CONTEXT_USER_ID;
    private final long vacancyId = DEFAULT_VACANCY_ID;

    private final Project project = Project.builder()
            .id(projectId)
            .build();

    private final Vacancy vacancy = Vacancy.builder()
            .name("Test VacancyForTest")
            .id(vacancyId)
            .position(TeamRole.DESIGNER)
            .project(project)
            .count(DEFAULT_VACANCY_COUNT)
            .status(VacancyStatus.OPEN)
            .description("VacancyForTest")
            .workSchedule(WorkSchedule.FLEXIBLE)
            .build();

    private final CreateVacancyDto createVacancyDto = CreateVacancyDto.builder()
            .name("VacancyForTest")
            .description("VacancyForTest")
            .projectId(projectId)
            .position(TeamRole.DESIGNER)
            .status(VacancyStatus.OPEN)
            .count(DEFAULT_VACANCY_COUNT)
            .workSchedule(WorkSchedule.FLEXIBLE)
            .build();

    private final UpdateVacancyDto updateVacancyDto = UpdateVacancyDto.builder()
            .name("VacancyForTest")
            .description("VacancyForTest")
            .projectId(projectId)
            .position(TeamRole.DESIGNER)
            .vacancyStatus(VacancyStatus.OPEN)
            .count(DEFAULT_VACANCY_COUNT)
            .workSchedule(WorkSchedule.FULL_TIME)
            .build();

    private final SearchVacancyDto searchVacancyDto = SearchVacancyDto.builder()
            .position(TeamRole.DESIGNER)
            .vacancyName("Test VacancyForTest")
            .build();

    @Mock
    private VacancyValidator vacancyValidator;

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private VacancyFilter nameFilter;

    @Mock
    private VacancyFilter positionFilter;

    @Spy
    private final VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    private VacancyServiceImpl vacancyService;

    @Captor
    private ArgumentCaptor<Vacancy> vacancyCaptor;

    @BeforeEach
    void prepareData() {
        vacancyService = new VacancyServiceImpl(vacancyRepository,
                vacancyMapper,
                vacancyValidator,
                userContext,
                List.of(nameFilter, positionFilter));
    }

    @Test
    void testVacancyCreateValidationError() {
        when(userContext.getUserId()).thenReturn(correctContextUserId);
        doThrow(new VacancyValidationException("User has an incorrect role"))
                .when(vacancyValidator).validateCreate(userId, projectId);

        assertThrows(VacancyValidationException.class,
                () -> vacancyService.create(createVacancyDto));
        verify(vacancyRepository, never()).save(vacancy);
    }

    @Test
    void testSuccessfullyVacancyCreated() {
        when(userContext.getUserId()).thenReturn(userId);

        vacancyService.create(createVacancyDto);

        verify(vacancyValidator, times(1)).validateCreate(userId, projectId);
        verify(vacancyRepository, times(1)).save(vacancyCaptor.capture());
        Vacancy savedVacancy = vacancyCaptor.getValue();

        assertEquals(createVacancyDto.name(), savedVacancy.getName());
        assertEquals(createVacancyDto.description(), savedVacancy.getDescription());
        assertEquals(createVacancyDto.count(), savedVacancy.getCount());
        assertEquals(createVacancyDto.position(), savedVacancy.getPosition());
        assertEquals(createVacancyDto.workSchedule(), savedVacancy.getWorkSchedule());
        assertEquals(createVacancyDto.projectId(), savedVacancy.getProject().getId());
        assertEquals(createVacancyDto.status(), savedVacancy.getStatus());
    }

    @Test
    void testSuccessfullyVacancyUpdated() {
        when(vacancyRepository.getReferenceById(vacancyId)).thenReturn(vacancy);
        when(userContext.getUserId()).thenReturn(userId);

        VacancyDto resultVacancyDto = vacancyService.update(vacancyId, updateVacancyDto);

        verify(vacancyValidator, times(1)).validateUpdate(userId, vacancy, updateVacancyDto);
        verify(vacancyMapper, times(1)).update(updateVacancyDto, vacancy);
        verify(vacancyRepository, times(1)).save(vacancyCaptor.capture());

        Vacancy updatedVacancy = vacancyCaptor.getValue();

        assertEquals(updatedVacancy.getId(), resultVacancyDto.id());
        assertEquals(updatedVacancy.getName(), resultVacancyDto.name());
        assertEquals(updatedVacancy.getCount(), resultVacancyDto.count());
        assertEquals(updatedVacancy.getStatus(), resultVacancyDto.status());
        assertEquals(updatedVacancy.getPosition(), resultVacancyDto.position());
        assertEquals(updatedVacancy.getCandidates(), resultVacancyDto.candidates());
        assertEquals(updatedVacancy.getDescription(), resultVacancyDto.description());
        assertEquals(updatedVacancy.getWorkSchedule(), resultVacancyDto.workSchedule());
        assertEquals(updatedVacancy.getProject().getId(), resultVacancyDto.projectId());
    }

    @Test
    void testVacancyUpdateValidationError() {
        when(vacancyRepository.getReferenceById(vacancyId)).thenReturn(vacancy);
        when(userContext.getUserId()).thenReturn(userId);
        doThrow(new VacancyValidationException("Can't update vacancy"))
                .when(vacancyValidator)
                .validateUpdate(userId,
                        vacancy,
                        updateVacancyDto);

        assertThrows(VacancyValidationException.class,
                () -> vacancyService.update(vacancyId, updateVacancyDto));
        verify(vacancyRepository, never()).save(vacancy);
    }

    @Test
    void testVacanciesFilteredAllStream() {
        when(vacancyRepository.findAll()).thenReturn(List.of(vacancy));
        when(nameFilter.isApplicable(searchVacancyDto)).thenReturn(true);
        when(positionFilter.isApplicable(searchVacancyDto)).thenReturn(true);
        when(nameFilter.apply(any(), eq(searchVacancyDto))).thenReturn(Stream.of(vacancy));
        when(positionFilter.apply(any(), eq(searchVacancyDto))).thenReturn(Stream.of(vacancy));
        List<VacancyDto> vacancies = List.of(vacancyMapper.toVacancyDto(vacancy));

        List<VacancyDto> resultVacancies = vacancyService.filterVacancies(searchVacancyDto);

        assertEquals(vacancies, resultVacancies);
        assertEquals(1, resultVacancies.size());

        VacancyDto resultDto = resultVacancies.get(0);

        assertNotNull(resultDto);
        assertEquals(TeamRole.DESIGNER, resultDto.position());
        assertTrue(resultDto.name().contains("Test VacancyForTest"));
    }

    @Test
    void testVacancyNotFound() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> vacancyService.getVacancyById(vacancyId));
    }

    @Test
    void testSuccessfullyVacancyGetById() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));
        VacancyDto expectedDto = vacancyMapper.toVacancyDto(vacancy);

        VacancyDto resultDto = vacancyService.getVacancyById(vacancyId);

        assertEquals(expectedDto, resultDto);
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }
}
