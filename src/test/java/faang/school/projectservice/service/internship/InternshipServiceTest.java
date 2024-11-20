package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internship.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;


public class InternshipServiceTest {

    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;

    @Spy
    private InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Captor
    private ArgumentCaptor<Internship> captor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateWithCheckingDurationInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setProjectId(null);
        testCheckDatePeriod(internshipDto);
        Assertions.assertThrows(DataValidationException.class, () -> internshipService.create(internshipDto));
    }

    @Test
    public void testCreateWithCheckingTeamMember() {
        // Создаем DTO стажировки
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setProjectId(1L); // ID проекта
        testCheckDatePeriod(internshipDto);
        internshipDto.setInternsIds(List.of(1L, 2L)); // IDs участников
        internshipDto.setMentorId(1L); // ID ментора

        // Создаем моки TeamMember
        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(1L); // ID участника
        teamMember1.setUserId(1L); // ID пользователя
        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(2L);
        teamMember2.setUserId(2L);

        when(teamMemberRepository.findById(1L)).thenReturn(teamMember1);
        when(teamMemberRepository.findById(2L)).thenReturn(teamMember2);

        Internship savedInternship = new Internship();
        savedInternship.setId(1L);
        savedInternship.setInterns(List.of(teamMember1, teamMember2)); // Устанавливаем список участников

        when(internshipRepository.save(any(Internship.class))).thenReturn(savedInternship);

        internshipService.create(internshipDto); // Вызываем метод создания стажировки

        verify(internshipRepository, times(1)).save(captor.capture());

        Internship capturedInternship = captor.getValue();

        Assertions.assertNotNull(capturedInternship.getInterns(), "Interns list should not be null");
        Assertions.assertEquals(
                internshipDto.getInternsIds(),
                capturedInternship.getInterns().stream().map(TeamMember::getUserId).collect(Collectors.toList()),
                "Interns IDs should match"
        );

        boolean mentorInTeam = capturedInternship.getInterns().stream()
                .anyMatch(teamMember -> teamMember.getUserId().equals(internshipDto.getMentorId()));
        Assertions.assertTrue(mentorInTeam, "Mentor should be part of the team");
    }

    @Test
    public void testUpdateInternshipSuccess() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        internshipDto.setStartDate(LocalDateTime.now().plusDays(5)); // Дата начала в будущем
        internshipDto.setEndDate(LocalDateTime.now().plusMonths(1));
        internshipDto.setInternsIds(List.of(1L, 2L));

        Internship existingInternship = new Internship();
        existingInternship.setId(1L);
        existingInternship.setStartDate(LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0));
        existingInternship.setEndDate(LocalDateTime.of(2022, Month.APRIL, 1, 0, 0));
        existingInternship.setStatus(InternshipStatus.COMPLETED); // Завершаем стажировку

        TeamMember teamMember1 = new TeamMember();
        teamMember1.setId(1L);
        teamMember1.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));
        teamMember1.setStages(List.of());

        TeamMember teamMember2 = new TeamMember();
        teamMember2.setId(2L);
        teamMember2.setRoles(new ArrayList<>(List.of(TeamRole.INTERN)));
        teamMember2.setStages(List.of());

        when(internshipRepository.findById(1L)).thenReturn(Optional.of(existingInternship));
        when(teamMemberRepository.findById(1L)).thenReturn(teamMember1);
        when(teamMemberRepository.findById(2L)).thenReturn(teamMember2);
        when(internshipRepository.save(any(Internship.class))).thenReturn(existingInternship);

        internshipService.updateInternship(internshipDto);

        verify(internshipRepository, times(1)).save(existingInternship);
        Assertions.assertEquals(internshipDto.getStartDate(), existingInternship.getStartDate());
        Assertions.assertEquals(internshipDto.getEndDate(), existingInternship.getEndDate());
        Assertions.assertEquals(2, existingInternship.getInterns().size());
    }

    @Test
    public void testUpdateInternshipThrowsWhenAddingInternsToStarted() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        internshipDto.setInternsIds(List.of(3L));

        Internship existingInternship = new Internship();
        existingInternship.setId(1L);
        existingInternship.setStartDate(LocalDateTime.now().minusDays(10));
        existingInternship.setStatus(InternshipStatus.IN_PROGRESS);

        when(internshipRepository.findById(1L)).thenReturn(Optional.of(existingInternship));

        DataValidationException exception = Assertions.assertThrows(DataValidationException.class,
                () -> internshipService.updateInternship(internshipDto));
        Assertions.assertEquals("стажировка началась, добавление новых стажеров невозможно", exception.getMessage());
    }

    @Test
    public void testGetAllInternshipByStatus() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        InternshipFilterDto filter = new InternshipFilterDto();
        filter.setStatus(InternshipStatus.IN_PROGRESS);
        filter.setIntern(TeamRole.INTERN);

        Internship internship = new Internship();
        internship.setId(1L);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setProject(project);
        TeamMember intern = new TeamMember();
        intern.setRoles(List.of(TeamRole.INTERN));
        internship.setInterns(List.of(intern));

        when(internshipRepository.findAll()).thenReturn(List.of(internship));
        when(internshipMapper.toDto(any(Internship.class))).thenReturn(new InternshipDto());

        List<InternshipDto> result = internshipService.getAllInternshipByStatus(projectId, filter);

        Assertions.assertEquals(1, result.size());
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllInternship() {
        Internship internship1 = new Internship();
        internship1.setId(1L);
        Internship internship2 = new Internship();
        internship2.setId(2L);

        when(internshipRepository.findAll()).thenReturn(List.of(internship1, internship2));
        when(internshipMapper.toListDto(anyList())).thenReturn(List.of(new InternshipDto(), new InternshipDto()));

        List<InternshipDto> result = internshipService.getAllInternship();

        Assertions.assertEquals(2, result.size());
        verify(internshipRepository, times(1)).findAll();
        verify(internshipMapper, times(1)).toListDto(anyList());
    }

    @Test
    public void testGetInternshipByIdSuccess() {
        Long internshipId = 1L;
        Internship internship = new Internship();
        internship.setId(internshipId);

        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));
        when(internshipMapper.toDto(any(Internship.class))).thenReturn(new InternshipDto());

        InternshipDto result = internshipService.getInternshipById(internshipId);

        Assertions.assertNotNull(result);
        verify(internshipRepository, times(1)).findById(internshipId);
        verify(internshipMapper, times(1)).toDto(internship);
    }

    @Test
    public void testGetInternshipByIdThrowsExceptionWhenNotFound() {
        Long internshipId = 1L;
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> internshipService.getInternshipById(internshipId));
        Assertions.assertEquals("Интернатура с ID 1 не найдена", exception.getMessage());
    }

    private void testCheckDatePeriod(InternshipDto internshipDto) {
        internshipDto.setStartDate(LocalDateTime.of(2022, Month.JANUARY, 1, 0, 0));
        internshipDto.setEndDate(LocalDateTime.of(2022, Month.APRIL, 1, 0, 0));
    }
}
