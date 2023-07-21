package faang.school.projectservice.mappper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VacancyMapperTest {

    @Autowired
    VacancyMapper vacancyMapper;

    @Test
    public void testToDto() {
        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .name("A")
                .project(Project.builder().id(1L).build())
                .createdBy(1L)
                .build();

        VacancyDto expected = new VacancyDto(1L, "A", 1L, 1L);

        assertEquals(expected, vacancyMapper.toDto(vacancy));
    }

}