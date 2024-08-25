package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.InternshipService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private InternshipValidator internshipValidator;

    @Mock
    private InternshipMapper internshipMapper;

    @Mock
    private InternshipFilter internshipFilter;

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
            .build();
        internshipFilterDto = InternshipFilterDto.builder()
            .internshipStatus(InternshipStatus.COMPLETED)
            .teamRole(TeamRole.DEVELOPER)
            .build();
        internship = new Internship();
        project = new Project();
        teamMember = new TeamMember();
        internships = List.of(internship);
        internshipService = new InternshipService(
            internshipRepository,
            projectRepository,
            teamMemberRepository,
            internshipValidator,
            internshipMapper,
            List.of(internshipFilter)
        );
    }

    @Test
    @DisplayName("Create Internship - Success")
    void createInternship_Success() {
        when(internshipMapper.toEntity(any(InternshipDto.class))).thenReturn(internship);
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberRepository.findById(anyLong())).thenReturn(teamMember);
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);
        when(internshipMapper.toDto(any(Internship.class))).thenReturn(internshipDto);

        InternshipDto result = internshipService.createInternship(internshipDto);

        verify(internshipValidator).validateDto(any(InternshipDto.class));
        verify(internshipValidator).validateInternshipDuration(any(InternshipDto.class));
        verify(internshipMapper).toEntity(any(InternshipDto.class));
        verify(projectRepository).getProjectById(anyLong());
        verify(teamMemberRepository).findById(anyLong());
        verify(internshipRepository).save(any(Internship.class));
        verify(internshipMapper).toDto(any(Internship.class));

        assertNotNull(result);
        assertEquals(internshipDto, result);
    }

    @Test
    @DisplayName("Update Internship - Success")
    void updateInternship_Success() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship));
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberRepository.findById(anyLong())).thenReturn(teamMember);
        when(internshipRepository.save(any(Internship.class))).thenReturn(internship);
        when(internshipMapper.toDto(any(Internship.class))).thenReturn(internshipDto);

        InternshipDto result = internshipService.updateInternship(internshipId, internshipDto);

        verify(internshipValidator).validateDto(any(InternshipDto.class));
        verify(internshipValidator).validateInternshipDuration(any(InternshipDto.class));
        verify(internshipRepository).findById(anyLong());
        verify(internshipValidator).validateInternshipNotStarted(any(InternshipDto.class));
        verify(projectRepository).getProjectById(anyLong());
        verify(teamMemberRepository).findById(anyLong());
        verify(internshipRepository).save(any(Internship.class));
        verify(internshipMapper).toDto(any(Internship.class));

        assertNotNull(result);
        assertEquals(internshipDto, result);
    }

    @Test
    @DisplayName("Update Internship - Not Found")
    void updateInternship_NotFound() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> internshipService.updateInternship(internshipId, internshipDto));

        verify(internshipRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Get Internships By Filter - Success")
    void getInternshipsByFilter_Success() {
        when(internshipRepository.findAll()).thenReturn(internships);
        when(internshipFilter.isApplicable(any(InternshipFilterDto.class))).thenReturn(true);
        when(internshipFilter.apply(any(), any(InternshipFilterDto.class))).thenReturn(internships.stream());
        when(internshipMapper.toDto(any(Internship.class))).thenReturn(internshipDto);

        List<InternshipDto> result = internshipService.getInternshipsByFilter(internshipFilterDto);

        verify(internshipRepository).findAll();
        verify(internshipFilter).isApplicable(any(InternshipFilterDto.class));
        verify(internshipFilter).apply(any(), any(InternshipFilterDto.class));
        verify(internshipMapper).toDto(any(Internship.class));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(internshipDto, result.get(0));
    }

    @Test
    @DisplayName("Get All Internships - Success")
    void getAllInternships_Success() {
        List<Internship> internships = List.of(internship);
        when(internshipRepository.findAll()).thenReturn(internships);
        when(internshipMapper.toDtoList(anyList())).thenReturn(List.of(internshipDto));

        List<InternshipDto> result = internshipService.getAllInternships();

        verify(internshipRepository).findAll();
        verify(internshipMapper).toDtoList(anyList());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(internshipDto, result.get(0));
    }

    @Test
    @DisplayName("Get Internship By ID - Success")
    void getInternshipById_Success() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship));
        when(internshipMapper.toDto(any(Internship.class))).thenReturn(internshipDto);

        InternshipDto result = internshipService.getInternshipById(internshipId);

        verify(internshipRepository).findById(anyLong());
        verify(internshipMapper).toDto(any(Internship.class));

        assertNotNull(result);
        assertEquals(internshipDto, result);
    }

    @Test
    @DisplayName("Get Internship By ID - Not Found")
    void getInternshipById_NotFound() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            internshipService.getInternshipById(internshipId)
        );

        verify(internshipRepository).findById(anyLong());
    }
}
