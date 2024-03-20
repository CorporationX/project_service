package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class VacancyMapperTest {
    private Vacancy vacancy;
    private VacancyDto vacancyDto;
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @BeforeEach
    void setUp() {
        vacancy = Vacancy.builder()
                .id(1L)
                .name("test")
                .project(Project.builder()
                        .id(1L)
                        .build())
                .build();
        vacancyDto = VacancyDto.builder()
                .id(1L)
                .name("test")
                .projectId(1L)
                .build();
    }

    @Test
    void testToDto() {
        VacancyDto vacancyDto1 = vacancyMapper.toDto(vacancy);
        assertEquals(vacancyDto, vacancyDto1);
    }

    @Test
    void testToEntity() {
        Vacancy vacancy2 = Vacancy.builder()
                .id(1L)
                .name("test")
                .build();
        Vacancy vacancy1 = vacancyMapper.toEntity(vacancyDto);
        assertEquals(vacancy2, vacancy1);
    }
}
