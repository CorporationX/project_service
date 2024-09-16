package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.intership.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    @InjectMocks
    private InternshipServiceImpl internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private InternshipValidator internshipValidator;

    @Mock
    private InternshipFilter internshipFilter;

    @Mock
    private InternshipMapperImpl internshipMapper;

    private long internshipId;
    private InternshipDto internshipDto;
    private InternshipFilterDto internshipFilterDto;
    private Internship internship;
    private Project project;
    private TeamMember teamMember;
    List<Internship> internships;


    @BeforeEach
    void setUp() {
        internshipId = 1;
        long projectId = 1;
        long mentorId = 1;

        internshipDto = InternshipDto.builder()
                .id(internshipId)
                .projectId(projectId)
                .mentorId(mentorId)
                .internsId(List.of())
                .build();

        internshipFilterDto = InternshipFilterDto.builder()
                .internshipStatus(InternshipStatus.COMPLETED)
                .teamRole(TeamRole.DEVELOPER)
                .build();

        internship = new Internship();
        project = new Project();
        teamMember = new TeamMember();
        internships = List.of(internship);

        internshipService = new InternshipServiceImpl(
                internshipRepository,
                internshipMapper,
                teamMemberRepository,
                List.of(internshipFilter),
                internshipValidator,
                projectRepository
        );
    }

    @Test
    @DisplayName("Create Internship - Success")
    void createInternship_Success() {
        doReturn(internship).when(internshipMapper).toEntity(any(InternshipDto.class));
        doReturn(project).when(projectRepository).getProjectById(anyLong());
        doReturn(teamMember).when(teamMemberRepository).findById(anyLong());
        doReturn(internship).when(internshipRepository).save(any(Internship.class));
        doReturn(internshipDto).when(internshipMapper).toDto(any(Internship.class));

        var result = internshipService.createInternship(internshipDto);

        verify(internshipValidator).validateDto(any(InternshipDto.class));
        verify(internshipValidator).validateInternshipDuration(any(InternshipDto.class));
        verify(internshipMapper).toEntity(any(InternshipDto.class));
        verify(projectRepository).getProjectById(anyLong());
        verify(teamMemberRepository).findById(anyLong());
        verify(internshipRepository).save(any(Internship.class));
        verify(internshipMapper).toDto(any(Internship.class));

        assertThat(result).isNotNull();
        assertThat(internshipDto).isEqualTo(result);
    }

    @Test
    @DisplayName("Update Internship - Success")
    void updateInternship_Success() {
        doReturn(Optional.of(internship)).when(internshipRepository).findById(anyLong());
        doReturn(project).when(projectRepository).getProjectById(anyLong());
        doReturn(teamMember).when(teamMemberRepository).findById(anyLong());
        doReturn(internship).when(internshipRepository).save(any(Internship.class));
        doReturn(internshipDto).when(internshipMapper).toDto(any(Internship.class));

        var result = internshipService.updateInternship(internshipId, internshipDto);

        verify(internshipValidator).validateDto(any(InternshipDto.class));
        verify(internshipValidator).validateInternshipDuration(any(InternshipDto.class));
        verify(internshipRepository).findById(anyLong());
        verify(internshipValidator).validateInternshipNotStarted(any(InternshipDto.class));
        verify(projectRepository).getProjectById(anyLong());
        verify(teamMemberRepository).findById(anyLong());
        verify(internshipRepository).save(any(Internship.class));
        verify(internshipMapper).toDto(any(Internship.class));

        assertThat(result).isNotNull();
        assertThat(internshipDto).isEqualTo(result);
    }

    @Test
    @DisplayName("Update Internship - Not Found")
    void updateInternship_NotFound() {
        doReturn(Optional.empty()).when(internshipRepository).findById(anyLong());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                internshipService.updateInternship(internshipId, internshipDto));

        verify(internshipRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Get All Internships - Success")
    void getAllInternships_Success() {
        doReturn(internships).when(internshipRepository).findAll();

        List<InternshipDto> result = internshipService.getAllInternships();

        verify(internshipRepository).findAll();
        verify(internshipMapper).toDto(internship);

        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
    }

    @Test
    void getAllInternshipsByFilter() {
    }

    @Test
    void getInternshipById() {
    }
}