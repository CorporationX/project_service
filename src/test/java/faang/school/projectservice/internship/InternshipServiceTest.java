package faang.school.projectservice.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.DataFormatException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipDto internshipDto;

    @Mock
    private Internship internship;

    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 1, 1, 0, 0);
    private static final LocalDateTime END_DATE_BAD = LocalDateTime.of(2020, 5, 31, 23, 59);
    private static final LocalDateTime END_DATE_GOOD = LocalDateTime.of(2020, 3, 31, 23, 59);

    @InjectMocks
    private InternshipService internshipService;

    @BeforeEach
    void setUp() {
        internshipDto = new InternshipDto();
        internship = new Internship();
    }

    //Positive tests

    @Test
    @DisplayName("создание стажировки")
    public void create_successfulTest() throws DataFormatException {
        //arrange
        internship.setId(0L);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internship.setInterns(List.of(new TeamMember()));
        internship.setProject(new Project());
        internship.setMentorId(new TeamMember());
        internship.setStartDate(START_DATE);
        internship.setEndDate(END_DATE_GOOD);

        internshipDto.setInterns(List.of(new TeamMember()));
        internshipDto.setProject(new Project());
        internshipDto.setMentorId(new TeamMember());
        internshipDto.setStartDate(START_DATE);
        internshipDto.setEndDate(END_DATE_GOOD);

        // When
        internshipService.create(internshipDto);

        // Then
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    @DisplayName("получение стажировок")
    public void getInternships_successfulTest() {
        //arrange
        internship.setId(1L);
        internshipDto.setId(1L);

        //when
        when(internshipRepository.findAll()).thenReturn(List.of(internship));
        List<InternshipDto> internshipDtos = List.of(internshipDto);

        //then
        Assertions.assertEquals(internshipService.getInternships(), internshipDtos);
    }

    @Test
    @DisplayName("получение стажировки по ID")
    public void getInternshipByID_successfulTest() throws DataFormatException {
        //arrange
        long internshipId = 1L;
        internship.setId(internshipId);
        internshipDto.setId(internshipId);

        //when
        when(internshipRepository.existsById(internshipId)).thenReturn(true);
        when(internshipRepository.findById(internshipId)).thenReturn(Optional.of(internship));

        //then
        Assertions.assertEquals(internshipService.getInternshipByID(internshipId), internshipDto);
    }

    @Test
    @DisplayName("получение стажировок по Status")
    public void getInternshipStatus_successfulTest() throws DataFormatException {
        //arrange
        long internshipId = 0L;
        long sinternshipId = 1L;

        internship.setStatus(InternshipStatus.COMPLETED);
        internship.setId(internshipId);

        Internship sinternship = new Internship();
        sinternship.setStatus(InternshipStatus.IN_PROGRESS);
        sinternship.setId(sinternshipId);

        internshipDto.setId(internshipId);
        internshipDto.setStatus(InternshipStatus.COMPLETED);

        //when
        when(internshipRepository.findAll()).thenReturn(List.of(internship, sinternship));

        //then
        Assertions.assertEquals(internshipService.getInternshipStatus(InternshipStatus.COMPLETED), List.of(internshipDto));
    }

    @Test
    @DisplayName("обновление стажировки успешное прохождение")
    public void update_goodInternshipTest() throws DataFormatException {
        //arrange
        Project project = new Project();
        project.setId(0L);
        project.setTasks(List.of());

        TeamMember teamMember = new TeamMember();
        teamMember.setId(0L);
        teamMember.setRoles(List.of(TeamRole.INTERN));

        internship.setId(0L);
        internship.setStatus(InternshipStatus.COMPLETED);
        internship.setInterns(List.of(teamMember));
        internship.setProject(project);
        internship.setMentorId(new TeamMember());
        internship.setStartDate(START_DATE);
        internship.setEndDate(END_DATE_GOOD);

        internshipDto.setInterns(List.of(teamMember));
        internshipDto.setProject(project);
        internshipDto.setMentorId(new TeamMember());
        internshipDto.setStartDate(START_DATE);
        internshipDto.setEndDate(END_DATE_GOOD);

        // When
        when(internshipRepository.existsById(0L)).thenReturn(true);
        internshipService.update(internshipDto, Map.of(0L, TeamRole.DEVELOPER));

        // Then
        verify(internshipRepository, times(1)).save(internship);
    }


    //Negative tests

    @Test
    @DisplayName("null test")
    public void validateInternship_nullTest() {
        internshipDto = null;

        Assertions.assertThrows(NullPointerException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("null project test")
    public void validateInternship_nullProjectTest() {
        internshipDto.setProject(null);

        Assertions.assertThrows(NullPointerException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("null mentor test")
    public void validateInternship_nullMentorTest() {
        internshipDto.setMentorId(null);

        Assertions.assertThrows(NullPointerException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("null startDate test")
    public void validateInternship_nullStartDateTest() {
        internshipDto.setStartDate(null);

        Assertions.assertThrows(NullPointerException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("null endDate test")
    public void validateInternship_nullEndDateTest() {
        internshipDto.setEndDate(null);

        Assertions.assertThrows(NullPointerException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("empty interns")
    public void validateInternship_internEmptyTest() {
        //arrange
        internshipDto.setProject(new Project());
        internshipDto.setMentorId(new TeamMember());
        internshipDto.setInterns(List.of());
        internshipDto.setStartDate(LocalDateTime.MIN);

        //then
        Assertions.assertThrows(IllegalArgumentException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("неправильное время")
    public void validateInternship_throwDateTest() {
        //arrange
        internshipDto.setProject(new Project());
        internshipDto.setMentorId(new TeamMember());
        internshipDto.setInterns(List.of(new TeamMember()));
        internshipDto.setStartDate(LocalDateTime.MAX);
        internshipDto.setEndDate(LocalDateTime.MIN);

        //then
        Assertions.assertThrows(DataFormatException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("неправильный период")
    public void validateInternship_throwPeriodTest() {
        //arrange
        internshipDto.setProject(new Project());
        internshipDto.setMentorId(new TeamMember());
        internshipDto.setInterns(List.of(new TeamMember()));

        internshipDto.setStartDate(START_DATE);
        internshipDto.setEndDate(END_DATE_BAD);

        //then
        Assertions.assertThrows(DataFormatException.class, () -> internshipService.validateInternship(internshipDto));
    }

    @Test
    @DisplayName("нет пользователя в бд")
    public void exitInternship_throwTest() {
        //arrange
        internshipDto.setId(0);

        //when
        when(internshipRepository.existsById(internshipDto.getId())).thenReturn(false);

        //then
        Assertions.assertThrows(DataFormatException.class, () -> internshipService.exitInternship(internshipDto.getId()));
    }
}
