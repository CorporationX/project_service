package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InternshipMapperTest {
    private final InternshipMapper internshipMapper = new InternshipMapperImpl();

    @Test
    public void mapToInternship() {
        Internship internship = Internship.builder()
                .id(1L)
                .name("test")
                .build();
        InternshipDto internshipDto = InternshipDto.builder()
                .id(1L)
                .name("test")
                .build();
        internshipMapper.toInternship(internshipDto);
        assertEquals(internship.getId(), internshipDto.getId());
        assertEquals(internship.getName(), internshipDto.getName());
    }

    @Test
    public void mapToInternshipDto() {
        Internship internship = Internship.builder()
                .id(1L)
                .name("test")
                .role(TeamRole.ANALYST)
                .build();
        InternshipDto internshipDto = InternshipDto.builder()
                .id(1L)
                .name("test")
                .role(TeamRole.ANALYST)
                .build();
        internshipMapper.toInternshipDto(internship);
        assertEquals(internship.getId(), internshipDto.getId());
        assertEquals(internship.getName(), internshipDto.getName());
        assertEquals(internship.getRole(), internshipDto.getRole());
    }
}
