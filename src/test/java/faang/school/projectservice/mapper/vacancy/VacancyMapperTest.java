package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alexander Bulgakov
 */
@ExtendWith(MockitoExtension.class)
public class VacancyMapperTest {
    private final VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Test
    @DisplayName("Test mapper to dto")
    public void testToDto() {
        Project project = new Project();
        project.setId(1L);

        Candidate candidate = new Candidate();
        candidate.setId(1L);

        List<Candidate> candidates = List.of(candidate);

        Vacancy vacancy = getVacancy(project, candidates);

        VacancyDto vacancyDto = vacancyMapper.toDto(vacancy);

        assertEquals(vacancy.getId(), vacancyDto.getId());
        assertEquals(vacancy.getName(), vacancyDto.getName());
        assertEquals(vacancy.getDescription(), vacancyDto.getDescription());
        assertEquals(vacancy.getPosition(), vacancyDto.getPosition());
        assertEquals(vacancy.getProject().getId(), vacancyDto.getProjectId());
        assertEquals(vacancy.getCount(), vacancyDto.getCandidatesCount());
        assertEquals(vacancy.getSalary(), vacancyDto.getSalary());
        assertEquals(vacancy.getWorkSchedule(), vacancyDto.getWorkSchedule());
        assertEquals(vacancy.getRequiredSkillIds(), vacancyDto.getRequiredSkillIds());
        assertEquals(vacancy.getStatus(), vacancyDto.getStatus());
    }

    @Test
    @DisplayName("Test mapper to entity")
    public void testToEntity() {
        VacancyDto vacancyDto = getVacancyDto();

        Project project = new Project();
        project.setId(1L);

        Vacancy vacancy = vacancyMapper.toEntity(vacancyDto);
        vacancy.setProject(project);

        assertEquals(vacancyDto.getId(), vacancy.getId());
        assertEquals(vacancyDto.getName(), vacancy.getName());
        assertEquals(vacancyDto.getDescription(), vacancy.getDescription());
        assertEquals(vacancyDto.getPosition(), vacancy.getPosition());
        assertEquals(vacancyDto.getProjectId(), vacancy.getProject().getId());
        assertEquals(vacancyDto.getSalary(), vacancy.getSalary());
        assertEquals(vacancyDto.getWorkSchedule(), vacancy.getWorkSchedule());
        assertEquals(vacancyDto.getRequiredSkillIds(), vacancy.getRequiredSkillIds());
        assertEquals(vacancyDto.getStatus(), vacancy.getStatus());
    }

    @NotNull
    private static Vacancy getVacancy(Project project, List<Candidate> candidates) {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setName("Software Engineer");
        vacancy.setDescription("Job description");
        vacancy.setPosition(TeamRole.DEVELOPER);
        vacancy.setProject(project);
        vacancy.setCandidates(candidates);
        vacancy.setCount(5);
        vacancy.setSalary(5000.0);
        vacancy.setWorkSchedule(WorkSchedule.FULL_TIME);
        vacancy.setRequiredSkillIds(new ArrayList<>());
        vacancy.setStatus(VacancyStatus.OPEN);
        return vacancy;
    }

    @NotNull
    private static VacancyDto getVacancyDto() {
        VacancyDto vacancyDto = new VacancyDto();
        vacancyDto.setId(1L);
        vacancyDto.setName("Software Engineer");
        vacancyDto.setDescription("Job description");
        vacancyDto.setPosition(TeamRole.DEVELOPER);
        vacancyDto.setProjectId(1L);
        vacancyDto.setCandidatesCount(5);
        vacancyDto.setSalary(5000.0);
        vacancyDto.setWorkSchedule(WorkSchedule.FULL_TIME);
        vacancyDto.setRequiredSkillIds(new ArrayList<>());
        vacancyDto.setStatus(VacancyStatus.OPEN);
        return vacancyDto;
    }
}
