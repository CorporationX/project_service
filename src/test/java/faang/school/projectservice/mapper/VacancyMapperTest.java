package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VacancyMapperTest {
    private VacancyMapper vacancyMapper;

    private Candidate candidate1;
    private Candidate candidate2;
    private List<Candidate> candidates;

    @BeforeEach
    public void setUp() {
        vacancyMapper = new VacancyMapperImpl();
        candidate1 = new Candidate();
        candidate1.setId(1L);
        candidate2 = new Candidate();
        candidate2.setId(2L);
        candidates = List.of(candidate1, candidate2);
    }

    @Test
    public void testToCreateDto() {
        // arrange
        Vacancy vacancy = new Vacancy().builder()
                .id(1L)
                .name("Java Developer")
                .description("Java Developer")
                .project(new Project())
                .candidates(candidates)
                .createdAt(LocalDateTime.now())
                .createdBy(1L)
                .status(VacancyStatus.OPEN)
                .count(1)
                .salary(1000.0)
                .build();
        List<Long> candidatesIds = candidates.stream().map(Candidate::getId).toList();

        // act
        CreateVacancyDto dto = vacancyMapper.toCreateDto(vacancy);

        // assert
        assertEquals(vacancy.getId(), dto.id());
        assertEquals(vacancy.getName(), dto.name());
        assertEquals(vacancy.getDescription(), dto.description());
        assertEquals(vacancy.getProject().getId(), dto.projectId());
        assertEquals(candidatesIds, dto.candidatesIds());
        assertEquals(vacancy.getStatus(), dto.status());
        assertEquals(vacancy.getCreatedBy(), dto.createdBy());
    }

    @Test
    public void testToEntity() {
        // arrange
        CreateVacancyDto dto = new CreateVacancyDto(1L, "Java Developer", "Java Developer", 1L, List.of(1L, 2L), VacancyStatus.OPEN, 1L, 1);

        // act
        Vacancy vacancy = vacancyMapper.toEntity(dto);

        // assert
        assertEquals(dto.id(), vacancy.getId());
        assertEquals(dto.name(), vacancy.getName());
        assertEquals(dto.description(), vacancy.getDescription());
        assertEquals(dto.status(), vacancy.getStatus());
        assertEquals(dto.createdBy(), vacancy.getCreatedBy());
    }
}
