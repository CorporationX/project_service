package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyCandidateMapper;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.TeamRole.DESIGNER;
import static faang.school.projectservice.model.TeamRole.MANAGER;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceTest {
    private Vacancy vacancy;
    private VacancyFilterDto filterDto;
    private final List<Candidate> candidates = List.of();
    private TeamMember creator;
    private Project project;

    @Mock
    private VacancyDto vacancyDto;

    @Mock
    private VacancyRepository repository;

    @Mock
    TeamMemberRepository memberRepository;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    private UserContext context;

    @Spy
    private VacancyMapper mapper;

    @Mock
    private VacancyCandidateMapper candidateMapper;

    @InjectMocks
    VacancyService service;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("1L");

        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Designer");
        vacancy.setSalary(10.5);
        vacancy.setCount(5);

        creator = new TeamMember();
        creator.setRoles(List.of(OWNER));

    }

    @Test
    public void positiveCreateVacancy() {
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(vacancyDto.getProjectId()).thenReturn(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(mapper.toEntity(vacancyDto)).thenReturn(vacancy);
        when(repository.save(vacancy)).thenReturn(vacancy);
        when(candidateMapper.toDto(vacancy))
                .thenReturn(new VacancyCandidateDto(
                        1L, 5, "Designer", candidates, DESIGNER));

        VacancyCandidateDto result = service.createVacancy(vacancyDto);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Designer", result.name());

        verify(projectRepository, times(1)).findById(1L);
        verify(repository, times(1)).save(vacancy);
    }

    @Test
    public void negativeCreateUserNotFound() {
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception =
                assertThrows(DataValidationException.class, () -> service.createVacancy(vacancyDto));

        assertEquals("Пользователь не найден.", exception.getMessage());
        verify(memberRepository, times(1)).findById(1L);
        verify(context, times(1)).getUserId();
    }

    @Test
    public void negativeNoHaveRequiredRoleForCreate() {
        TeamMember designer = new TeamMember();
        designer.setRoles(List.of(DESIGNER));
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(designer));

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                service.createVacancy(vacancyDto));

        assertEquals("Вы не имеете прав на публикацию вакансий.", exception.getMessage());
        verify(memberRepository, times(1)).findById(1L);
        verify(context, times(1)).getUserId();
    }

    @Test
    public void negativeCreateProjectNotFound() {
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(vacancyDto.getProjectId()).thenReturn(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                service.createVacancy(vacancyDto));

        assertEquals("Проект не найден.", exception.getMessage());
        verify(memberRepository, times(1)).findById(1L);
        verify(context, times(1)).getUserId();
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    public void negativeCreateNameIsBlank() {
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("");
        vacancy.setSalary(10.5);
        vacancy.setCount(5);

        DataValidationException exception = prepareBadValidationDataForCreate();

        assertEquals("Имя не может быть пустым", exception.getMessage());
        verify(projectRepository, times(1)).findById(1L);
        verify(repository, times(0)).save(vacancy);
    }

    @Test
    public void negativeCreateSalaryIsZero() {
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Vacancy");
        vacancy.setSalary(0.00);
        vacancy.setCount(5);

        DataValidationException exception = prepareBadValidationDataForCreate();

        assertEquals("Заработная плата должна быть больше 0", exception.getMessage());
        verify(projectRepository, times(1)).findById(1L);
        verify(repository, times(0)).save(vacancy);
    }

    @Test
    public void negativeCreateVacancyCountZero() {
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Vacancy");
        vacancy.setSalary(1.00);
        vacancy.setCount(0);

        DataValidationException exception = prepareBadValidationDataForCreate();

        assertEquals("Количество вакантных мест не может быть меньше 1", exception.getMessage());
        verify(projectRepository, times(1)).findById(1L);
        verify(repository, times(0)).save(vacancy);
    }

    @Test
    public void positiveUpdateVacancy() {
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(repository.findById(1L)).thenReturn(Optional.of(vacancy));
        doNothing().when(mapper).updateVacancyFromDto(vacancyDto, vacancy);
        when(repository.save(vacancy)).thenReturn(vacancy);

        service.updateVacancy(1L, vacancyDto);

        verify(repository, times(1)).findById(1L);
        verify(memberRepository, times(1)).findById(1L);
        verify(context, times(2)).getUserId();
        verify(mapper, times(1)).updateVacancyFromDto(vacancyDto, vacancy);
        verify(repository, times(1)).save(vacancy);
    }

    @Test
    public void negativeNoHaveRequiredRoleForUpdate() {
    }

    @Test
    public void negativeUpdateUserAlreadyExistInCompany() {
        Vacancy vacancy = mock(Vacancy.class);
        Candidate candidate = new Candidate();
        candidate.setUserId(2L);

        when(repository.findById(1L)).thenReturn(Optional.of(vacancy));
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));

        TeamMember candidateMember = new TeamMember();
        candidateMember.setId(2L);
        candidateMember.setRoles(List.of(TeamRole.DEVELOPER));
        when(memberRepository.findById(2L)).thenReturn(Optional.of(candidateMember));

        VacancyDto vacancyDto = VacancyDto.builder()
                .candidates(List.of(candidate))
                .build();

        Exception exception = assertThrows(DataValidationException.class, () -> {
            service.updateVacancy(1L, vacancyDto);
        });

        assertEquals("Пользователь состоит в компании", exception.getMessage());
    }

    @Test
    public void negativeUpdateVacancyIsNull() {
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));

        Exception exception = assertThrows(DataValidationException.class, () -> {
            service.updateVacancy(1L, null);
        });

        assertEquals("Вакансия не может быть пустой", exception.getMessage());

        verify(context, times(1)).getUserId();
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    public void negativeUpdateVacancyNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));

        Exception exception = assertThrows(DataValidationException.class, () -> {
            service.updateVacancy(1L, vacancyDto);
        });

        assertEquals("Вакансия не найдена", exception.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(context, times(1)).getUserId();
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    public void negativeUpdateVacancyNameNull() {
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("");
        vacancy.setSalary(10.5);
        vacancy.setCount(5);

        DataValidationException exception = prepareBadValidationDataForUpdate();

        assertEquals("Имя не может быть пустым", exception.getMessage());
    }

    @Test
    public void negativeUpdateVacancySalaryZero() {
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Name");
        vacancy.setSalary(0.0);
        vacancy.setCount(5);

        DataValidationException exception = prepareBadValidationDataForUpdate();

        assertEquals("Заработная плата должна быть больше 0", exception.getMessage());
    }

    @Test
    public void negativeUpdateVacancyCountZero() {
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Name");
        vacancy.setSalary(1.0);
        vacancy.setCount(0);

        DataValidationException exception = prepareBadValidationDataForUpdate();

        assertEquals("Количество вакантных мест не может быть меньше 1", exception.getMessage());
    }

    @Test
    public void positiveFindVacancy() {
        List<Vacancy> vacancyList = new ArrayList<>();
        when(repository.findAll()).thenReturn(vacancyList);
        List<VacancyCandidateDto> result = service.findVacancy(filterDto);
        assertNotNull(result);
        verify(repository, times(1)).findAll();
    }

    @Test
    public void positiveGetInfoAboutVacancy() {
        long vacancyId = 1L;
        Vacancy infoVacancy = Vacancy.builder().id(vacancyId).name("vacancy").build();
        VacancyCandidateDto resultDto = new VacancyCandidateDto(
                1L, 4, "vacancy", candidates, MANAGER);
        when(repository.findById(vacancyId)).thenReturn(Optional.of(infoVacancy));
        when(candidateMapper.toDto(infoVacancy)).thenReturn(resultDto);
        VacancyCandidateDto result = service.getVacancyInfoById(vacancyId);
        assertNotNull(resultDto);
        assertEquals(1L, result.id());
        assertEquals("vacancy", result.name());
        verify(repository, times(1)).findById(vacancyId);
    }

    @Test
    public void negativeNotFoundedVacancy() {
        long vacancyId = 1;
        when(repository.findById(vacancyId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                service.getVacancyInfoById(vacancyId));

        assertEquals("Вакансия не найдена", exception.getMessage());
        verify(repository, times(1)).findById(vacancyId);
        verify(candidateMapper, times(0)).toDto(any());
    }

    private DataValidationException prepareBadValidationDataForCreate() {
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(vacancyDto.getProjectId()).thenReturn(1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(mapper.toEntity(vacancyDto)).thenReturn(vacancy);

        return assertThrows(DataValidationException.class, () ->
                service.createVacancy(vacancyDto));

    }

    private DataValidationException prepareBadValidationDataForUpdate() {
        when(context.getUserId()).thenReturn(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(repository.findById(1L)).thenReturn(Optional.of(vacancy));
        doNothing().when(mapper).updateVacancyFromDto(vacancyDto, vacancy);

        return assertThrows(DataValidationException.class, () ->
                service.updateVacancy(1L, vacancyDto));
    }

}
