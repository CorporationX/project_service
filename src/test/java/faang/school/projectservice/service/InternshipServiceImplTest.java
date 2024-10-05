package faang.school.projectservice.service;

import faang.school.projectservice.dto.intership.InternshipDto;
import faang.school.projectservice.dto.intership.InternshipFilterDto;
import faang.school.projectservice.dto.intership.TeamMemberDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.InternshipFilters;
import faang.school.projectservice.service.internship.InternshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipServiceImplTest {

    @InjectMocks
    InternshipServiceImpl internshipService;

    @Mock
    InternshipRepository internshipRepository;

    @Mock
    InternshipMapper internshipMapper;

    @Mock
    TeamMemberMapper teamMemberMapper;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    List<InternshipFilters> internshipFilters;

    private InternshipDto internshipDto;
    private Internship internship;
    private Internship internship2;
    private InternshipDto internshipDto2;

    @BeforeEach
    void setUp() {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(2);
        internshipDto = new InternshipDto(1L, 3L,
                new TeamMemberDto(1L, 4L), List.of(), startDate, endDate,
                InternshipStatus.COMPLETED, 999L, "name", "description");

        internshipDto2 = new InternshipDto(1L, 3L,
                new TeamMemberDto(1L, 4L), List.of(), startDate, endDate, InternshipStatus.IN_PROGRESS,
                999L, "name", "description");

        internship = new Internship();
        internship.setId(1L);
        internship.setStatus(COMPLETED);
        internship.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        internship.setEndDate(LocalDateTime.of(2023, 1, 31, 0, 0));
        internship.setDescription("Описание");
        internship.setName("Название");
        internship.setInterns(new ArrayList<>());

        internship2 = new Internship();
        internship2.setId(1L);
        internship2.setStatus(InternshipStatus.IN_PROGRESS);
        internship2.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        internship2.setEndDate(LocalDateTime.of(2023, 1, 31, 0, 0));
        internship2.setDescription("Описание");
        internship2.setName("Название");
        internship2.setInterns(new ArrayList<>());
    }

    @Test
    void testCreate() {

        when(projectRepository.getProjectById(internshipDto.getProjectId())).thenReturn(new Project());
        when(internshipMapper.toEntity(internshipDto)).thenReturn(new Internship());
        when(teamMemberMapper.toEntity(internshipDto.getMentor())).thenReturn(new TeamMember());

        internshipService.create(internshipDto);

        Mockito.verify(internshipRepository).save(any(Internship.class));
    }

    @Test
    void testUpdateCompletedStatus() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship));

        InternshipDto result = internshipService.update(internshipDto);

        assertNotNull(result);
        assertEquals(COMPLETED, result.getStatus());
        Mockito.verify(internshipRepository, times(1)).save(any(Internship.class));
    }

    @Test
    void testUpdateInProgressStatus() {
        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship2));

        InternshipDto result = internshipService.update(internshipDto2);

        assertNotNull(result);
        assertEquals(InternshipStatus.IN_PROGRESS, result.getStatus());
        Mockito.verify(internshipRepository, times(1)).save(any(Internship.class));
    }

    @Test
    void testFilter() {
        InternshipFilterDto internshipFilterDto = new InternshipFilterDto();
        List<Internship> internships = List.of(internship, internship2);

        when(internshipRepository.findAll()).thenReturn(internships);

        List<InternshipDto> dtos = List.of(internshipDto2, internshipDto);

        when(internshipMapper.toDtoList(any())).thenReturn(dtos);

        List<InternshipDto> result = internshipService.getInternshipByFilter(internshipFilterDto);

        assertEquals(dtos, result);

        verify(internshipRepository).findAll();
        verify(internshipMapper).toDtoList(any());
    }

    @Test
    void testGetAllInternships() {
        when(internshipRepository.findAll()).thenReturn(List.of(new Internship()));
        internshipService.getAllInternships(internshipDto);
        Mockito.verify(internshipRepository).findAll();
    }

    @Test
    void testGetInternshipById() {
        when(internshipRepository.findById(internshipDto.getId())).thenReturn(Optional.of(new Internship()));
        internshipService.getInternshipById(internshipDto);
        Mockito.verify(internshipRepository).findById(internshipDto.getId());
    }
}