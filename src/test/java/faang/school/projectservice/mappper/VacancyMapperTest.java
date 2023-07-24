package faang.school.projectservice.mappper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class VacancyMapperTest {
    @Autowired
    VacancyMapper vacancyMapper;

    VacancyDto vacancyDto;

    @BeforeEach
    public void setUp() {
        vacancyDto = new VacancyDto(1L, "A", 1L, 1L, 1L, VacancyStatus.OPEN);
    }

    @Test
    public void testToDto() {
        Vacancy vacancy = Vacancy
                .builder()
                .id(1L)
                .name("A")
                .project(Project.builder().id(1L).build())
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .build();

        assertEquals(vacancyDto, vacancyMapper.toDto(vacancy));
    }

    @Test
    public void testToModel() {
        Vacancy expected = Vacancy
                .builder()
                .id(1L)
                .name("A")
                .createdBy(1L)
                .updatedBy(1L)
                .status(VacancyStatus.OPEN)
                .build();

        assertEquals(expected, vacancyMapper.toModel(vacancyDto));
    }

}