package faang.school.projectservice.internship;

import faang.school.projectservice.model.dto.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.entity.Internship;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.TeamMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InternshipMapperTest {

    private InternshipMapper internshipMapper;

    @BeforeEach
    void setUp() {
        internshipMapper = Mappers.getMapper(InternshipMapper.class);
    }

    @Test
    void testToDto_WithValidInternship() {
        Project project = new Project();
        project.setId(1L);

        TeamMember mentor = new TeamMember();
        mentor.setUserId(2L);

        TeamMember intern1 = new TeamMember();
        intern1.setUserId(3L);

        TeamMember intern2 = new TeamMember();
        intern2.setUserId(4L);

        var internship = new Internship();
        internship.setProject(project);
        internship.setMentor(mentor);
        internship.setCreatedBy(5L);
        internship.setInterns(Arrays.asList(intern1, intern2));

        InternshipDto internshipDto = internshipMapper.toDto(internship);

        assertEquals(1L, internshipDto.getProjectId());
        assertEquals(2L, internshipDto.getMentorUserId());
        assertEquals(5L, internshipDto.getCreatorUserId());
        assertEquals(2, internshipDto.getInternUserIds().size());
        assertEquals(3L, internshipDto.getInternUserIds().get(0));
        assertEquals(4L, internshipDto.getInternUserIds().get(1));
    }

    @Test
    void testToDto_WithNullInternship() {
        InternshipDto internshipDto = internshipMapper.toDto(null);

        assertNull(internshipDto);
    }

    @Test
    void testToDtoList_WithValidInternships() {
        Project project = new Project();
        project.setId(1L);

        TeamMember mentor = new TeamMember();
        mentor.setUserId(2L);

        TeamMember intern1 = new TeamMember();
        intern1.setUserId(3L);

        Internship internship1 = new Internship();
        internship1.setProject(project);
        internship1.setMentor(mentor);
        internship1.setCreatedBy(5L);
        internship1.setInterns(Arrays.asList(intern1));

        Internship internship2 = new Internship();
        internship2.setProject(project);
        internship2.setMentor(mentor);
        internship2.setCreatedBy(6L);
        internship2.setInterns(Arrays.asList(intern1));

        List<Internship> internships = Arrays.asList(internship1, internship2);

        List<InternshipDto> internshipDtos = internshipMapper.toDtoList(internships);

        assertEquals(2, internshipDtos.size());
        assertEquals(1L, internshipDtos.get(0).getProjectId());
        assertEquals(2L, internshipDtos.get(0).getMentorUserId());
        assertEquals(5L, internshipDtos.get(0).getCreatorUserId());
        assertEquals(1, internshipDtos.get(0).getInternUserIds().size());
        assertEquals(3L, internshipDtos.get(0).getInternUserIds().get(0));

        assertEquals(1L, internshipDtos.get(1).getProjectId());
        assertEquals(2L, internshipDtos.get(1).getMentorUserId());
        assertEquals(6L, internshipDtos.get(1).getCreatorUserId());
        assertEquals(1, internshipDtos.get(1).getInternUserIds().size());
        assertEquals(3L, internshipDtos.get(1).getInternUserIds().get(0));
    }

    @Test
    void testToDtoList_WithNullInternships() {
        List<InternshipDto> internshipDtos = internshipMapper.toDtoList(null);

        assertNull(internshipDtos);
    }

    @Test
    void testMapTeamMembersToUserIds_WithValidTeamMembers() {
        TeamMember intern1 = new TeamMember();
        intern1.setUserId(3L);

        TeamMember intern2 = new TeamMember();
        intern2.setUserId(4L);

        List<Long> userIds = internshipMapper.mapTeamMembersToUserIds(Arrays.asList(intern1, intern2));

        assertEquals(2, userIds.size());
        assertEquals(3L, userIds.get(0));
        assertEquals(4L, userIds.get(1));
    }

    @Test
    void testMapTeamMembersToUserIds_WithNullTeamMembers() {
        List<Long> userIds = internshipMapper.mapTeamMembersToUserIds(null);

        assertNull(userIds);
    }
}

