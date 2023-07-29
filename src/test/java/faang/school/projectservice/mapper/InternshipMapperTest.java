package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class InternshipMapperTest {

    private static final long INTERNSHIP_ID = 1L;
    private static final long MENTOR_ID = 2L;
    private static final long INTERN_ID_1 = 232L;
    private static final long INTERN_ID_2 = 2121L;
    private static final long INTERN_ID_3 = 1232L;
    private static final long PROJECT_ID = 3L;

    @InjectMocks
    private InternshipMapperImpl internshipMapper;


    @Test
    public void toDtoTest() {
        Internship internship = new Internship();
        internship.setId(INTERNSHIP_ID);
        internship.setProject(Project.builder().id(PROJECT_ID).build());
        internship.setMentorId(TeamMember.builder().id(MENTOR_ID).build());
        internship.setInterns(List.of(
            TeamMember.builder().id(INTERN_ID_1).build(),
            TeamMember.builder().id(INTERN_ID_2).build(),
            TeamMember.builder().id(INTERN_ID_3).build()
        ));
        internship.setStatus(InternshipStatus.IN_PROGRESS);

        InternshipDto internshipDto = internshipMapper.toDto(internship);

        assertEquals(INTERNSHIP_ID, internshipDto.getId().longValue());
        assertEquals(PROJECT_ID, internshipDto.getProjectId().longValue());
        assertEquals(MENTOR_ID, internshipDto.getMentorId().longValue());
        assertEquals(List.of(INTERN_ID_1, INTERN_ID_2, INTERN_ID_3), internshipDto.getInternIds());
        assertEquals(InternshipStatus.IN_PROGRESS, internshipDto.getStatus());
    }
}