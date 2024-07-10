package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class VacancyMapperTest {

    private final VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Test
    public void testVacancyToVacancyDto() {
        Project project = new Project();
        project.setId(1L);
        Candidate candidate1 = new Candidate();
        candidate1.setId(1L);
        Candidate candidate2 = new Candidate();
        candidate2.setId(2L);
        List<Candidate> candidates = Arrays.asList(candidate1, candidate2);

        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 0, 0);

        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .name("Software Engineer")
                .description("Job description")
                .project(project)
                .candidates(candidates)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .createdBy(1L)
                .updatedBy(2L)
                .status(VacancyStatus.OPEN)
                .salary(100000.0)
                .workSchedule(WorkSchedule.FULL_TIME)
                .count(1)
                .requiredSkillIds(Arrays.asList(1L, 2L))
                .build();

        VacancyDto vacancyDto = vacancyMapper.toDto(vacancy);

        assertNotNull(vacancyDto);
        assertEquals(vacancy.getId(), vacancyDto.getId());
        assertEquals(vacancy.getName(), vacancyDto.getName());
        assertEquals(vacancy.getDescription(), vacancyDto.getDescription());
        assertEquals(vacancy.getProject().getId(), vacancyDto.getProjectId());
        assertEquals(vacancy.getCandidates().size(), vacancyDto.getCandidatesIds().size());
        assertEquals(vacancy.getCandidates().get(0).getId(), vacancyDto.getCandidatesIds().get(0));
        assertEquals(vacancy.getCandidates().get(1).getId(), vacancyDto.getCandidatesIds().get(1));
        assertEquals(vacancy.getCreatedAt(), vacancyDto.getCreatedAt());
        assertEquals(vacancy.getUpdatedAt(), vacancyDto.getUpdatedAt());
        assertEquals(vacancy.getStatus().name(), vacancyDto.getStatus());
        assertEquals(vacancy.getSalary(), vacancyDto.getSalary());
        assertEquals(vacancy.getWorkSchedule().name(), vacancyDto.getWorkSchedule());
        assertEquals(vacancy.getRequiredSkillIds().size(), vacancyDto.getRequiredSkillIds().size());
        assertEquals(vacancy.getRequiredSkillIds().get(0), vacancyDto.getRequiredSkillIds().get(0));
        assertEquals(vacancy.getRequiredSkillIds().get(1), vacancyDto.getRequiredSkillIds().get(1));
    }

    @Test
    public void testVacancyDtoToVacancy() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 0, 0);

        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setName("Software Engineer");
        vacancyDto.setDescription("Job description");
        vacancyDto.setProjectId(1L);
        vacancyDto.setCandidatesIds(Arrays.asList(1L, 2L));
        vacancyDto.setCreatedAt(createdAt);
        vacancyDto.setUpdatedAt(updatedAt);
        vacancyDto.setStatus("OPEN");
        vacancyDto.setSalary(100000.0);
        vacancyDto.setWorkSchedule("FULL_TIME");
        vacancyDto.setRequiredSkillIds(Arrays.asList(1L, 2L));

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);

        assertNotNull(vacancy);
        assertEquals(vacancyDto.getId(), vacancy.getId());
        assertEquals(vacancyDto.getName(), vacancy.getName());
        assertEquals(vacancyDto.getDescription(), vacancy.getDescription());
        assertEquals(vacancyDto.getProjectId(), vacancy.getProject().getId());
        assertNull(vacancy.getCandidates());
        assertEquals(vacancyDto.getCreatedAt(), vacancy.getCreatedAt());
        assertEquals(vacancyDto.getUpdatedAt(), vacancy.getUpdatedAt());
        assertEquals(vacancyDto.getStatus(), vacancy.getStatus().name());
        assertEquals(vacancyDto.getSalary(), vacancy.getSalary());
        assertEquals(vacancyDto.getWorkSchedule(), vacancy.getWorkSchedule().name());
        assertEquals(vacancyDto.getRequiredSkillIds().size(), vacancy.getRequiredSkillIds().size());
        assertEquals(vacancyDto.getRequiredSkillIds().get(0), vacancy.getRequiredSkillIds().get(0));
        assertEquals(vacancyDto.getRequiredSkillIds().get(1), vacancy.getRequiredSkillIds().get(1));
    }
}