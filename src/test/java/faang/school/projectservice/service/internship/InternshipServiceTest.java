package faang.school.projectservice.service.internship;

import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.mapper.TeamMemberMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import faang.school.projectservice.service.validator.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private TeamMemberService teamMemberService;
    @Mock
    private InternshipValidator internshipValidator;
    @Mock
    private AuditorAwareImpl auditor;

    @InjectMocks
    private InternshipService internshipService;
    @Spy
    private InternshipMapperImpl mapper;
    @Spy
    private TeamMemberMapperImpl teamMemberMapper;

    private Internship internship;
    private InternshipDto internshipDto;
    private InternshipUpdateDto internshipUpdateDto;
    private Project project;
    private TeamMember mentor;
    private TeamMember intern;
    private List<TeamMember> interns;
    private Long createdBy = 1L;
    private TeamRole role = TeamRole.DEVELOPER;

    @BeforeEach
    public void setUp() {
        mentor = TeamMember.builder()
                .id(1L)
                .build();
        project = Project.builder()
                .id(1L)
                .tasks(List.of())
                .build();
        intern = TeamMember.builder()
                .id(2L)
                .build();
        interns = List.of(intern);

        internshipDto = InternshipDto.builder()
                .projectId(project.getId())
                .mentorId(mentor.getId())
                .internsIds(List.of(intern.getId()))
                .status(InternshipStatus.IN_PROGRESS)
                .role(role)
                .build();

        internshipUpdateDto = InternshipUpdateDto.builder()
                .internsIds(List.of(intern.getId()))
                .status(InternshipStatus.COMPLETED)
                .build();

        internship = mapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setMentor(mentor);
        internship.setInterns(interns);
        internship.setCreatedBy(createdBy);
    }

    @Test
    public void CreateInternshipSuccess() {
        when(projectService.getProjectById(project.getId())).thenReturn(project);
        when(teamMemberService.get(mentor.getId())).thenReturn(mentor);
        when(teamMemberService.get(intern.getId())).thenReturn(intern);
        when(auditor.getCurrentAuditor()).thenReturn(Optional.of(1L));

        internshipService.create(internshipDto);

        verify(internshipValidator, atLeastOnce()).internshipCreateValidate(internship);
        verify(internshipRepository, atLeastOnce()).save(internship);
        verify(projectService, atLeastOnce()).getProjectById(project.getId());
        verify(teamMemberService, atLeastOnce()).get(mentor.getId());
        verify(teamMemberService, atLeastOnce()).get(intern.getId());
    }

    @Test
    public void getInternInterns() {
        when(teamMemberService.get(intern.getId())).thenReturn(intern);

        List<TeamMember> interns = internshipService.getInterns(List.of(intern.getId()));
        assertEquals(intern, interns.get(0));
    }

    @Test
    public void getInternshipInternshipNotExists() {
        when(internshipRepository.findById(internship.getId())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> internshipService.get(internship.getId()));
        assertEquals(ex.getMessage(), "Стажировки с id: " + internship.getId() + " не существует");
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"
    })
    public void updateInternshipCompletionParametrizedTest(boolean isComplete) {
        when(internshipRepository.findById(internshipUpdateDto.getId())).thenReturn(Optional.of(internship));
        when(internshipValidator.internValidation(any(), any())).thenReturn(isComplete);
        when(auditor.getCurrentAuditor()).thenReturn(Optional.of(createdBy));
        TeamMemberDto teamMemberDto = teamMemberMapper.teamMemberToTeamMemberDto(intern);

        internshipService.update(internshipUpdateDto);

        verify(internshipRepository, atLeastOnce()).save(internship);
        if (isComplete) {
            teamMemberDto.setRoles(List.of(role));
//            verify(teamMemberService, atLeastOnce()).updateMember(teamMemberDto, createdBy, project.getId());
//            assertEquals(intern.getRoles().get(0), internship.getRole());
        } else {
            teamMemberDto.setRoles(List.of());
//            assertEquals(intern.getRoles(), List.of());
        }
        verify(teamMemberService, atLeastOnce()).updateMember(teamMemberDto, createdBy, project.getId());
    }

    @Test
    public void updateInternshipParticipantsUpdateTest() {
        TeamMember newIntern = TeamMember.builder()
                .id(3L)
                .build();
        Internship newInternship = mapper.toEntity(internshipDto);
        newInternship.setInterns(List.of(intern, newIntern));

        internshipUpdateDto.setInternsIds(List.of(intern.getId(), newIntern.getId()));
        internshipUpdateDto.setStatus(InternshipStatus.IN_PROGRESS);

        when(internshipRepository.findById(internshipUpdateDto.getId())).thenReturn(Optional.of(internship));
        when(teamMemberService.get(newIntern.getId())).thenReturn(newIntern);
        when(teamMemberService.get(intern.getId())).thenReturn(intern);
        when(internshipValidator.isInternsListNotEqualNotEmpty(internship, internshipUpdateDto)).thenReturn(true);
        when(auditor.getCurrentAuditor()).thenReturn(Optional.of(createdBy));

        internshipService.update(internshipUpdateDto);

        assertEquals(internship.getInterns(), newInternship.getInterns());
    }
}
