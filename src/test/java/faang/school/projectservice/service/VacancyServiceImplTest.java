package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.VacancyMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VacancyServiceImplTest {

    @Mock VacancyRepository       vacancyRepository;
    @Mock ProjectRepository       projectRepository;
    @Mock TeamMemberRepository    teamMemberRepository;
    @Mock UserContext             userContext;

    @Spy
    private CandidateMapper candidateMapper = Mappers.getMapper(CandidateMapper.class);

    @Spy
    private VacancyMapperImpl vacancyMapper = new VacancyMapperImpl();

    private VacancyServiceImpl vacancyService;    // we’ll build this by hand

    private final long PROJECT_ID = 1L;
    private final long USER_ID    = 42L;
    private final long VAC_ID     = 99L;

    private Project project;
    private Vacancy vacancy;
    private TeamMember ownerMember;
    private VacancyDto dto;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(vacancyMapper, "candidateMapper", candidateMapper);

        vacancyService = new VacancyServiceImpl(
                vacancyRepository,
                vacancyMapper,
                projectRepository,
                userContext,
                teamMemberRepository
        );

        dto = new VacancyDto();
        dto.setName("Backend Developer");
        dto.setDescription("API & DB");
        dto.setPosition(TeamRole.DEVELOPER);
        dto.setCount(2);

        vacancy = vacancyMapper.toEntity(dto);
        vacancy.setId(VAC_ID);
        vacancy.setStatus(VacancyStatus.OPEN);

        project = new Project();
        project.setId(PROJECT_ID);

        ownerMember = new TeamMember();
        ownerMember.setId(5L);
        ownerMember.setUserId(USER_ID);
        //ownerMember.setRoles(List.of(TeamRole.DEVELOPER));
        ownerMember.setRoles(List.of(TeamRole.OWNER));
    }

    @Test
    void createVacancy_whenUserIsOwner_saveAndReturnDto() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        when(vacancyRepository.save(any(Vacancy.class))).thenReturn(vacancy);

        VacancyDto vacancyDto = vacancyService.createVacancy(PROJECT_ID, dto);

        assertThat(vacancyDto.getId()).isEqualTo(VAC_ID);
        assertThat(vacancyDto.getName()).isEqualTo(dto.getName());
        assertThat(vacancyDto.getStatus()).isEqualTo(VacancyStatus.OPEN);
        verify(vacancyRepository).save(any());
    }

    @Test
    void createVacancy_whenUserNotInProject_throwsValidation() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(null);

        assertThatThrownBy(() -> vacancyService.createVacancy(PROJECT_ID, dto))
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
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(existing));
        when(vacancyRepository.save(any())).thenReturn(vacancy);

        VacancyDto updated = vacancyService.updateVacancy(PROJECT_ID, VAC_ID, dto);

        assertThat(updated.getId()).isEqualTo(VAC_ID);
        assertThat(updated.getPosition()).isEqualTo(dto.getPosition());
        verify(vacancyRepository).save(any());
    }

    @Test
    void updateVacancy_whenNotFound_throws() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vacancyService.updateVacancy(PROJECT_ID, VAC_ID, dto))
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
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(otherProjectVac));

        assertThatThrownBy(() -> vacancyService.updateVacancy(PROJECT_ID, VAC_ID, dto))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    void closeVacancy_withEnoughAccepted_marksClosed() {
        Vacancy v = new Vacancy();
        v.setId(VAC_ID);
        v.setProject(project);
        v.setCount(2);
        v.setCandidates(List.of(
                new Candidate() {
                    {
                        setCandidateStatus(CandidateStatus.ACCEPTED);
                    }
                },
                new Candidate() {
                    {
                        setCandidateStatus(CandidateStatus.ACCEPTED);
                    }
                }
        ));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(v));
        when(vacancyRepository.save(any())).thenReturn(v);

        VacancyDto closed = vacancyService.closeVacancy(PROJECT_ID, VAC_ID);
        assertThat(closed.getStatus()).isEqualTo(VacancyStatus.CLOSED);
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
                        setCandidateStatus(CandidateStatus.PENDING);
                    }
                }
        ));

        when(userContext.getUserId()).thenReturn(USER_ID);
        when(teamMemberRepository.findByUserIdAndProjectId(USER_ID, PROJECT_ID)).thenReturn(ownerMember);
        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> vacancyService.closeVacancy(PROJECT_ID, VAC_ID))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("Not enough accepted");
    }

    @Test
    void getVacancyById_valid_returnsDto() {
        Vacancy v = vacancyMapper.toEntity(dto);
        v.setId(VAC_ID);
        v.setProject(project);

        when(vacancyRepository.findById(VAC_ID)).thenReturn(Optional.of(v));

        VacancyDto got = vacancyService.getVacancyById(PROJECT_ID, VAC_ID);
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

        assertThatThrownBy(() -> vacancyService.getVacancyById(PROJECT_ID, VAC_ID))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("does not belong");
    }

    @Test
    void getVacanciesByProjectId_filtersByPositionAndName() {
        Vacancy v1 = vacancyMapper.toEntity(dto);
        v1.setId(1L);
        v1.setProject(project);
        v1.setPosition(TeamRole.DEVELOPER);
        v1.setName("Back dev");

        Vacancy v2 = vacancyMapper.toEntity(dto);
        v2.setId(2L);
        v2.setProject(project);
        v2.setPosition(TeamRole.MANAGER);
        v2.setName("Project manager");

        when(vacancyRepository.findAll()).thenReturn(List.of(v1, v2));

        var list = vacancyService.getVacanciesByProjectId(PROJECT_ID, "developer", "Back");
        assertThat(list).extracting(VacancyDto::getId).containsExactly(1L);
    }
}
