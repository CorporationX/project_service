package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.testData.internship.InternshipTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InternshipMapperTest {
    private final InternshipMapper internshipMapper = Mappers.getMapper(InternshipMapper.class);
    private InternshipDto internshipDto;
    private Internship internship;

    @BeforeEach
    void setUp() {
        InternshipTestData internshipTestData = new InternshipTestData();
        internshipDto = internshipTestData.getInternshipDto();
        internship = internshipTestData.getInternship();
    }

    @DisplayName("should map dto to entity ignoring project, mentor and interns")
    @Test
    void toEntityTest() {
        internship.setProject(null);
        internship.setMentorId(null);
        internship.setInterns(null);

        Internship actualEntity = internshipMapper.toEntity(internshipDto);

        assertEquals(internship, actualEntity);
    }

    @DisplayName("should map entity to dto")
    @Test
    void toDtoTest() {
        InternshipDto actualDto = internshipMapper.toDto(internship);

        assertEquals(internshipDto, actualDto);
    }

    @DisplayName("should update transmitted entity by passed dto")
    @Test
    void updateTest() {
        internshipDto.setName("New internship name");

        internshipMapper.updateEntity(internshipDto, internship);

        assertEquals("New internship name", internship.getName());
    }
}