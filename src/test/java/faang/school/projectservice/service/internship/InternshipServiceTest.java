package faang.school.projectservice.service.internship;

import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.InternshipService;
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
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private InternshipValidator internshipValidator;
    @Mock
    private AuditorAwareImpl auditor;

    @InjectMocks
    private InternshipService internshipService;
    @Spy
    private InternshipMapperImpl mapper;

    private Internship internship;
    private InternshipCreateDto internshipCreateDto;
    private InternshipUpdateDto internshipUpdateDto;
    private Project project;
    private TeamMember mentor;
    private TeamMember intern;
    private List<TeamMember> interns;
    private Long createdBy = 1L;

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

        internshipCreateDto = InternshipCreateDto.builder()
                .projectId(project.getId())
                .mentorId(mentor.getId())
                .internsId(List.of(intern.getId()))
                .status(InternshipStatus.IN_PROGRESS)
                .role(TeamRole.DEVELOPER)
                .build();

        internshipUpdateDto = InternshipUpdateDto.builder()
                .internsId(List.of(intern.getId()))
                .status(InternshipStatus.COMPLETED)
                .build();

        internship = mapper.toEntity(internshipCreateDto);
        internship.setProject(project);
        internship.setMentor(mentor);
        internship.setInterns(interns);
        internship.setCreatedBy(createdBy);
    }

    @Test
    public void CreateInternshipSuccess() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(teamMemberRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(teamMemberRepository.findById(intern.getId())).thenReturn(Optional.of(intern));
        when(auditor.getCurrentAuditor()).thenReturn(Optional.of(1L));

        internshipService.create(internshipCreateDto);

        verify(internshipValidator, atLeastOnce()).internshipCreateValidate(internship);
        verify(internshipRepository, atLeastOnce()).save(internship);
        verify(projectRepository, atLeastOnce()).findById(project.getId());
        verify(teamMemberRepository, atLeastOnce()).findById(mentor.getId());
        verify(teamMemberRepository, atLeastOnce()).findById(intern.getId());
    }

    @Test
    public void getProjectProjectNotExist() {
        when(projectRepository.findById(project.getId())).thenReturn(Optional.empty());
        ;

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> internshipService.getProject(project.getId()));
        assertEquals(ex.getMessage(), "Проекта с id: " + project.getId() + " не существует!!!");
    }

    @Test
    public void getMentorMentorNotExists() {
        when(teamMemberRepository.findById(mentor.getId())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> internshipService.getMentor(mentor.getId()));
        assertEquals(ex.getMessage(), "Ментора с id: " + mentor.getId() + " не существует!!!");
    }

    @Test
    public void getInternInternNotExists() {
        when(teamMemberRepository.findById(intern.getId())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> internshipService.getInterns(List.of(intern.getId())));
        assertEquals(ex.getMessage(), "Стажера с id: " + intern.getId() + " не существует!!!");
    }

    @Test
    public void getInternshipInternshipNotExists() {
        when(internshipRepository.findById(internship.getId())).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> internshipService.getInternship(internship.getId()));
        assertEquals(ex.getMessage(), "Стажировки с id: " + internship.getId() + " не существует");
    }

    @ParameterizedTest
    @CsvSource({
            "true",
            "false"
    })
    public void updateInternshipCompletionParametrizedTest(boolean isComplete) {
        mockInternshipUpdateDependencies();
        when(internshipValidator.internValidation(any(), any())).thenReturn(isComplete);

        internshipService.updateInternship(internshipUpdateDto);

        verify(teamMemberRepository, atLeastOnce()).save(intern);
        verify(internshipRepository, atLeastOnce()).save(internship);
        if (isComplete) {
            assertEquals(intern.getRoles().get(0), internship.getRole());
        } else {
            assertEquals(intern.getRoles(), List.of());
        }
    }

    @Test
    public void updateInternshipParticipantsUpdateTest() {
        TeamMember newIntern = TeamMember.builder()
                .id(3L)
                .build();
        Internship newInternship = mapper.toEntity(internshipCreateDto);
        newInternship.setInterns(List.of(intern, newIntern));

        internshipUpdateDto.setInternsId(List.of(intern.getId(), newIntern.getId()));
        internshipUpdateDto.setStatus(InternshipStatus.IN_PROGRESS);

        mockInternshipUpdateDependencies();
        when(teamMemberRepository.findById(newIntern.getId())).thenReturn(Optional.of(newIntern));
        when(teamMemberRepository.findById(intern.getId())).thenReturn(Optional.of(intern));
        when(internshipValidator.isInternsListNotEqualNotEmpty(internship, internshipUpdateDto)).thenReturn(true);

        internshipService.updateInternship(internshipUpdateDto);

        assertEquals(internship.getInterns(), newInternship.getInterns());
    }

    private void mockInternshipUpdateDependencies() {
        when(internshipRepository.findById(internshipUpdateDto.getId())).thenReturn(Optional.of(internship));
        when(mapper.toEntity(any())).thenReturn(internship);
    }
}
