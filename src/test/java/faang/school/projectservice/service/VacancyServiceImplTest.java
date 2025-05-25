package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceImplTest {

    @Mock
    VacancyRepository vacancyRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    TeamMemberRepository memberRepo;
    @Mock
    UserContext userContext;

    @Spy
    VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @InjectMocks
    VacancyServiceImpl service;

    private final long PROJECT_ID = 1L;
    private final long USER_ID = 42L;
    private final long VAC_ID = 99L;

    private Project project;
    private TeamMember ownerMember;
    private VacancyDto dto;
    private Vacancy vacancy;

    @BeforeEach
    void setUp() {

        dto = VacancyDto.builder()
                .name("Backend Developer")
                .description("API & DB")
                .position(TeamRole.DEVELOPER)
                .count(2)
                .build();

        vacancy = vacancyMapper.toEntity(dto);
        vacancy.setId(VAC_ID);
        vacancy.setStatus(VacancyStatus.OPEN);

        project = new Project();
        project.setId(PROJECT_ID);

        ownerMember = new TeamMember();
        ownerMember.setId(5L);
        ownerMember.setUserId(USER_ID);
        ownerMember.setRoles(List.of(TeamRole.OWNER));
    }

    @Test
    void createVacancy_whenUserIsOwner_savesAndReturnsDto() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(memberRepo.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);

        VacancyDto result = service.createVacancy(PROJECT_ID, dto);

        assertThat(result.getId()).isEqualTo(VAC_ID);
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getStatus()).isEqualTo(VacancyStatus.OPEN);
        verify(vacancyRepository).save(any());
    }

    @Test
    void createVacancy_whenUserNotInProject_throwsValidation() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(memberRepo.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(null);

        assertThatThrownBy(() -> service.createVacancy(PROJECT_ID, dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Not allowed");
        verifyNoInteractions(projectRepository, vacancyRepository);
    }

    @Test
    void updateVacancy_validData_updatesAndReturnsDto() {
        Vacancy existing = new Vacancy();
        existing.setId(VAC_ID);
        existing.setProject(project);
        existing.setStatus(VacancyStatus.OPEN);

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(memberRepo.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(existing));
        when(vacancyRepository.save(any())).thenReturn(vacancy);

        VacancyDto updated = service.updateVacancy(PROJECT_ID, VAC_ID, dto);

        assertThat(updated.getId()).isEqualTo(VAC_ID);
        assertThat(updated.getPosition()).isEqualTo(dto.getPosition());
        verify(vacancyRepository).save(any());
    }

    @Test
    void updateVacancy_whenNotFound_throws() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(memberRepo.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateVacancy(PROJECT_ID, VAC_ID, dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void updateVacancy_wrongProjectId_throws() {
        Vacancy otherProjectVac = new Vacancy();
        otherProjectVac.setId(VAC_ID);
        otherProjectVac.setProject(new Project() {{
            setId(PROJECT_ID + 1);
        }});
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(memberRepo.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(otherProjectVac));

        assertThatThrownBy(() -> service.updateVacancy(PROJECT_ID, VAC_ID, dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    void closeVacancy_notEnoughAccepted_throws() {
        Vacancy v = new Vacancy();
        v.setId(VAC_ID);
        v.setProject(project);
        v.setCount(3);
        v.setCandidates(List.of(
                new Candidate() {
                    {
                        setCandidateStatus(CandidateStatus.ACCEPTED);
                    }
                },
                new Candidate() {
                    {
                        setCandidateStatus(CandidateStatus.WAITING_RESPONSE);
                    }
                }
        ));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(memberRepo.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> service.closeVacancy(PROJECT_ID, VAC_ID))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Not enough accepted");
    }

    @Test
    void getVacancyById_valid_returnsDto() {
        Vacancy v = vacancyMapper.toEntity(dto);
        v.setId(VAC_ID);
        v.setProject(project);

        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(v));

        VacancyDto got = service.getVacancyById(PROJECT_ID, VAC_ID);
        assertThat(got.getId()).isEqualTo(VAC_ID);
    }

    @Test
    void getVacancyById_wrongProjectId_throws() {
        Vacancy v = vacancyMapper.toEntity(dto);
        v.setId(VAC_ID);
        v.setProject(new Project() {
            {
                setId(PROJECT_ID + 1);
            }
        });

        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> service.getVacancyById(PROJECT_ID, VAC_ID))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    void getVacanciesByProjectId_filters() {
        Vacancy v1 = new Vacancy();
        v1.setProject(new Project());
        v1.getProject().setId(1L);
        v1.setPosition(TeamRole.DEVELOPER);
        v1.setName("Alpha");

        Vacancy v2 = new Vacancy();
        v2.setProject(new Project());
        v2.getProject().setId(1L);
        v2.setPosition(TeamRole.MANAGER);
        v2.setName("Beta");

        when(vacancyRepository.findAll()).thenReturn(List.of(v1, v2));

        List<VacancyDto> all = service.getVacanciesByProjectId(1L, null, null);

        assertThat(all).hasSize(2);
        assertThat(all.get(0).getPosition()).isEqualTo(TeamRole.DEVELOPER);
        assertThat(all.get(1).getPosition()).isEqualTo(TeamRole.MANAGER);
        assertThat(all.get(0).getName()).isEqualTo("Alpha");
        assertThat(all.get(1).getName()).isEqualTo("Beta");
    }
}