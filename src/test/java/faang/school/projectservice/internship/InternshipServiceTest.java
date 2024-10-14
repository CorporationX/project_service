package faang.school.projectservice.internship;

import faang.school.projectservice.model.dto.InternshipDto;
import faang.school.projectservice.model.dto.InternshipFilterDto;
import faang.school.projectservice.filter.impl.InternshipStatusFilter;
import faang.school.projectservice.filter.impl.InternshipTeamRoleFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.entity.Internship;
import faang.school.projectservice.model.enums.InternshipStatus;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.Team;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.impl.InternshipServiceImpl;
import faang.school.projectservice.validator.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InternshipServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private InternshipValidator validator;
    @Mock
    private InternshipMapper internshipMapper;
    @Mock
    private InternshipStatusFilter internshipStatusFilter;
    @Mock
    private InternshipTeamRoleFilter internshipTeamRoleFilter;

    @InjectMocks
    private InternshipServiceImpl internshipService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        internshipService = new InternshipServiceImpl(
                projectRepository,
                internshipRepository,
                teamMemberRepository,
                teamRepository,
                taskRepository,
                List.of(internshipStatusFilter, internshipTeamRoleFilter), // Добавляем моки в список
                validator,
                internshipMapper
        );
    }

    @Test
    void testCreate_ShouldCreateInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setInternUserIds(List.of(1L, 2L));
        internshipDto.setMentorUserId(1L);
        internshipDto.setProjectId(1L);

        Project project = new Project();
        TeamMember mentor = new TeamMember();
        Team team = new Team();
        mentor.setTeam(team);

        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(teamMemberRepository.findByUserIdAndProjectId(anyLong(), anyLong())).thenReturn(mentor);
        when(internshipRepository.save(any(Internship.class))).thenReturn(new Internship());

        internshipService.create(internshipDto);

        verify(projectRepository).getProjectById(anyLong());
        verify(teamMemberRepository).findByUserIdAndProjectId(anyLong(), anyLong());
        verify(internshipRepository).save(any(Internship.class));
    }

    @Test
    void testUpdate_ShouldUpdateInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        internshipDto.setInternUserIds(List.of(1L, 2L));
        internshipDto.setMentorUserId(1L);
        internshipDto.setProjectId(1L);

        Internship internship = new Internship();
        internship.setStatus(InternshipStatus.PREPARATION);
        Team team = new Team();
        team.setId(1L);
        TeamMember mentor = new TeamMember();
        mentor.setUserId(internshipDto.getMentorUserId());
        team.addMember(mentor);
        internship.setMentor(mentor);
        TeamMember firstIntern = new TeamMember();
        firstIntern.setUserId(1L);
        TeamMember secondIntern = new TeamMember();
        secondIntern.setUserId(2L);
        List<TeamMember> interns = Arrays.asList(firstIntern, secondIntern);
        internship.setInterns(interns);
        Project project = new Project();
        project.setId(1L);
        internship.setProject(project);

        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship));

        internshipService.update(1L, internshipDto);

        verify(internshipRepository).findById(anyLong());
        verify(internshipRepository).save(any(Internship.class));
    }

    @Test
    void testGetInternshipsByProjectAndFilter_ShouldReturnFilteredInternships() {
        InternshipFilterDto filterDto = new InternshipFilterDto();
        Internship internship = new Internship();
        List<Internship> internshipList = List.of(internship);

        when(internshipRepository.findByProjectId(anyLong())).thenReturn(internshipList);

        when(internshipStatusFilter.isApplicable(any())).thenReturn(true);
        when(internshipStatusFilter.apply(any(), any())).thenReturn(internshipList.stream());

        when(internshipTeamRoleFilter.isApplicable(any())).thenReturn(false);

        List<InternshipDto> result = internshipService.getInternshipsByProjectAndFilter(1L, filterDto);

        assertNotNull(result);
        verify(internshipRepository).findByProjectId(anyLong());
        verify(internshipStatusFilter).apply(any(), any());
        verify(internshipTeamRoleFilter, never()).apply(any(), any()); // Этот фильтр не должен был применяться
    }

    @Test
    void testGetAllInternships_ShouldReturnPagedInternships() {
        Internship internship = new Internship();
        Page<Internship> internshipPage = new PageImpl<>(List.of(internship));
        when(internshipRepository.findAll(any(Pageable.class))).thenReturn(internshipPage);

        Page<InternshipDto> result = internshipService.getAllInternships(PageRequest.of(0, 10));

        assertNotNull(result);
        verify(internshipRepository).findAll(any(Pageable.class));
    }

    @Test
    void testGetInternshipById_ShouldReturnInternship() {
        Internship internship = new Internship();

        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(internshipMapper.toDto(internship)).thenReturn(new InternshipDto());

        InternshipDto result = internshipService.getInternshipById(1L);

        assertNotNull(result);
        verify(internshipRepository).findById(anyLong());
    }

    @Test
    void testGetInternshipById_ShouldThrowException_WhenNotFound() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> internshipService.getInternshipById(1L));
    }
}

