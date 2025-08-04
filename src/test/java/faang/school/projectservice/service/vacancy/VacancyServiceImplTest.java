package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.filter.vacancy.VacancyFilterServiceImpl;
import faang.school.projectservice.validation.vacancy.VacancyFilterDtoValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * VacancyServiceImplTest — тесты для {@link VacancyServiceImpl}.
 *
 * @author Myrza
 * @since 23.07.2025
 */
@ExtendWith(MockitoExtension.class)
public class VacancyServiceImplTest {
    @Mock
    private VacancyFilterServiceImpl filterService;
    @Mock
    private UserContext userContext;
    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private VacancyMapper vacancyMapper;
    @InjectMocks
    private VacancyServiceImpl service;
    @Mock
    private VacancyFilterDtoValidator filterDtoValidator;

    @Test
    @DisplayName("создания вакансии успешный кейс")
    void create_success() {
        var projectId = 1L;
        var project = VacancyServiceTestData.getProject(projectId, 2L, "Mega project");
        var userId = 1L;
        var createDto = VacancyServiceTestData.getCreateDto(
                "Java dev",
                "strong java dev",
                TeamRole.DEVELOPER,
                projectId,
                WorkSchedule.REMOTE
        );
        var vacancy = VacancyServiceTestData.toEntity(
                null,
                createDto,
                project,
                userId,
                VacancyStatus.OPEN
        );

        var createdVacancy = VacancyServiceTestData.toEntity(
                1L,
                createDto,
                project,
                userId,
                VacancyStatus.OPEN
        );
        var viewDto = VacancyServiceTestData.toVacancyViewDto(createdVacancy);

        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.getByIdOrThrow(projectId))
                .thenReturn(project);
        when(teamMemberRepository.isUserHasRole(eq(userId), eq(projectId), eq(TeamRole.MANAGER)))
                .thenReturn(true);
        when(vacancyMapper.toEntity(eq(createDto)))
                .thenReturn(vacancy);
        when(vacancyRepository.save(eq(vacancy)))
                .thenReturn(createdVacancy);
        when(vacancyMapper.toViewDto(eq(createdVacancy)))
                .thenReturn(viewDto);

        var actual = service.create(createDto);
        assertEquals(viewDto, actual);
    }

    @Test
    @DisplayName("создания вакансии провальный кейс; нет доступа")
    void create_forbidden() {
        var projectId = 1L;
        var project = VacancyServiceTestData.getProject(projectId, 2L, "Mega project");
        var userId = 3L;
        var createDto = VacancyServiceTestData.getCreateDto(
                "Java dev",
                "strong java dev",
                TeamRole.DEVELOPER,
                projectId,
                WorkSchedule.REMOTE
        );

        when(userContext.getUserId()).thenReturn(userId);
        when(projectRepository.getByIdOrThrow(projectId))
                .thenReturn(project);
        when(teamMemberRepository.isUserHasRole(eq(userId), eq(projectId), eq(TeamRole.MANAGER)))
                .thenReturn(false);

        var expectedMessage = "У пользователя нет доступа для создания вакансии в этом проекте";
        var thrown = assertThrows(ForbiddenException.class, () -> service.create(createDto));
        assertEquals(expectedMessage, thrown.getMessage());
    }

    @ParameterizedTest
    @DisplayName("обновление вакансии успешный кейс")
    @MethodSource("faang.school.projectservice.service.vacancy.VacancyServiceTestData#provideUpdateParams")
    void update_success(Long userId, VacancyUpdateDto updateDto, Project project,
                        Vacancy vacancyFromDb, Vacancy preUpdatedVacancy,
                        Vacancy updatedVacancy, VacancyDto expected) {
        var projectId = project.getId();
        var vacancyId = vacancyFromDb.getId();

        when(userContext.getUserId()).thenReturn(userId);
        when(vacancyRepository.getByIdOrThrow(vacancyId)).thenReturn(vacancyFromDb);
        when(teamMemberRepository.isUserHasRole(eq(userId), eq(projectId), eq(TeamRole.MANAGER)))
                .thenReturn(true);
        doAnswer(invocation -> {
            VacancyUpdateDto dto = invocation.getArgument(0);
            Vacancy v = invocation.getArgument(1);
            v.setName(dto.name());
            v.setDescription(dto.name());
            v.setPosition(dto.position());
            return null;
        }).when(vacancyMapper).update(eq(updateDto), eq(vacancyFromDb));

        when(vacancyRepository.save(eq(preUpdatedVacancy)))
                .thenReturn(updatedVacancy);

        when(vacancyMapper.toViewDto(eq(updatedVacancy)))
                .thenReturn(expected);

        var actual = service.update(vacancyId, updateDto);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @DisplayName("получение списка вакансии успешный кейс")
    @MethodSource("faang.school.projectservice.service.vacancy.VacancyServiceTestData#provideGetListParams")
    public void getList_success(VacancyFilterDto filterDto, List<Vacancy> repoResult,
                                List<Vacancy> filtered, List<VacancyDto> expected) {
        when(vacancyRepository.findAll()).thenReturn(repoResult);
        when(filterService.getFilteredList(eq(repoResult), eq(filterDto)))
                .thenReturn(filtered);
        for (int i = 0; i < filtered.size(); i++) {
            when(vacancyMapper.toViewDto(eq(filtered.get(i))))
                    .thenReturn(expected.get(i));
        }

        List<VacancyDto> actual = service.getList(filterDto);

        assertEquals(expected, actual);
    }
}
