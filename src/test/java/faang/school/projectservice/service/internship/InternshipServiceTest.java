package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;

    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Spy
    private InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);
    private InternshipDto internshipDto;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreateInternshipSuccessful() {

    }

    @Test
    void testCreateInternshipWithInvalidProject(){
        Project project = new Project();
        project.setId(5L);
        internshipDto = InternshipDto.builder()
                .project(project)
                .build();
        when(projectRepository.getProjectById(5L)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, ()-> internshipService.createInternship(internshipDto));
    }
    @Test
    void testCreateInternshipWithInvalidMentor(){
        TeamMember mentor = new TeamMember();
        mentor.setId(5L);
        internshipDto = InternshipDto.builder()
                .mentorId(mentor)
                .build();

    }
    @Test
    void testCreateInternshipWithNullInterns(){
        Project project = new Project();
        project.setId(5L);
        TeamMember mentor = new TeamMember();
        mentor.setId(5L);
        internshipDto = InternshipDto.builder()
                .project(project)
                .mentorId(mentor)
                .interns(null)
                .build();
        when(projectRepository.getProjectById(5L)).thenReturn(project);
        when(teamMemberRepository.findById(5L)).thenReturn(mentor);
        assertThrows(IllegalArgumentException.class,() -> internshipService.createInternship(internshipDto));
    }
    @Test
     void testCreateInternshipWithEmptyInterns(){
        Project project = new Project();
        project.setId(5L);
        TeamMember mentor = new TeamMember();
        mentor.setId(5L);
        internshipDto = InternshipDto.builder()
                .project(project)
                .mentorId(mentor)
                .interns(new ArrayList<>())
                .build();
        when(projectRepository.getProjectById(5L)).thenReturn(project);
        when(teamMemberRepository.findById(5L)).thenReturn(mentor);
        assertThrows(IllegalArgumentException.class,() -> internshipService.createInternship(internshipDto));

    }
    @Test
    void testCreateInternshipWithLongerThreeMonths(){
        Project project = new Project();
        project.setId(5L);
        TeamMember mentor = new TeamMember();
        mentor.setId(5L);
        List<TeamMember> interns = new ArrayList<>(List.of(new TeamMember(), new TeamMember()));
        internshipDto = InternshipDto.builder()
                .project(project)
                .mentorId(mentor)
                .interns(interns)
                .startDate()
                .endDate()
                .build();
        when(projectRepository.getProjectById(5L)).thenReturn(project);
        when(teamMemberRepository.findById(5L)).thenReturn(mentor);
        assertThrows(IllegalArgumentException.class,() -> internshipService.createInternship(internshipDto));
    }


}