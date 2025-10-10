package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.MANAGER;
import static faang.school.projectservice.model.TeamRole.OWNER;
import static faang.school.projectservice.model.TeamRole.TESTER;
import static faang.school.projectservice.model.VacancyStatus.OPEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private CandidateRepository candidateRepository;
    
    @InjectMocks
    private VacancyService vacancyService;

    private Project project;
    private Long userId;
    private TeamMember teamMember;
    private Vacancy vacancy;

    @BeforeEach
    public void setUp() {
        project = Project.builder()
                .id(1L)
                .name("TestProject1")
                .build();

        teamMember = TeamMember.builder()
                .id(1L)
                .userId(111L)
                .build();
        
        vacancy = Vacancy.builder()
                .id(1L)
                .name("TestVacancy1")
                .count(5)
                .description("description")
                .position(MANAGER)
                .status(OPEN)
                .build();
        
        userId = 111L;
    }
    
    @Test
    public void create_shouldCreateVacancy_successfully() {
        teamMember.setRoles(Arrays.asList(OWNER, MANAGER));

        Long projectId = project.getId();
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);
        when(projectRepository.getReferenceById(projectId)).thenReturn(project);

        when(vacancyRepository.save(any(Vacancy.class)))
                .thenAnswer(invocationOnMock -> {
                    Vacancy vacancyResult = invocationOnMock.getArgument(0);
                    vacancyResult.setId(1L);
                    return vacancyResult;
                });

        Vacancy result = vacancyService.create(vacancy, projectId);

        assertNotNull(result);
        assertEquals(OPEN, vacancy.getStatus());
        assertEquals(projectId, vacancy.getProject().getId());

        verify(vacancyRepository, times(1)).save(any(Vacancy.class));
        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        verify(projectRepository, times(1)).getReferenceById(projectId);
        verify(userContext, times(1)).getUserId();
    }

    @Test
    public void create_shouldCreateVacancy_failure() {
        teamMember.setRoles(Arrays.asList(TESTER, DEVELOPER));

        Long projectId = project.getId();
        when(userContext.getUserId()).thenReturn(userId);
        when(teamMemberRepository.findByUserIdAndProjectId(userId, projectId)).thenReturn(teamMember);

        assertThrows(ForbiddenException.class, () -> vacancyService.create(vacancy, projectId));

        verify(teamMemberRepository, times(1)).findByUserIdAndProjectId(userId, projectId);
        verify(projectRepository, times(0)).getReferenceById(projectId);
        verify(userContext, times(1)).getUserId();
        verify(vacancyRepository, times(0)).save(any(Vacancy.class));
    }

}