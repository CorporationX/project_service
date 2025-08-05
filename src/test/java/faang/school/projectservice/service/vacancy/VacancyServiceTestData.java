package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.candidate.CandidateDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class VacancyServiceTestData {
    private static final Random RANDOM = new Random();

    public static Project getProject(Long id, Long ownerId, String name) {
        var project = new Project();
        project.setId(id);
        project.setOwnerId(ownerId);
        project.setName(name);
        return project;
    }

    public static VacancyCreateDto getCreateDto(String name, String description, TeamRole position,
                                                Long projectId, WorkSchedule workSchedule) {
        return new VacancyCreateDto(
                name,
                description,
                position,
                projectId,
                RANDOM.nextInt(1, 4),
                RANDOM.nextDouble(75_000.0, 500_000.0),
                workSchedule,
                List.of(1L, 2L, 3L),
                UUID.randomUUID().toString()
        );
    }

    public static Vacancy toEntity(Long id, VacancyCreateDto createDto, Project project,
                                   Long createdBy, VacancyStatus status) {
        var vacancy = new Vacancy();
        vacancy.setId(id);
        vacancy.setCreatedBy(createdBy);
        vacancy.setCreatedAt(LocalDateTime.now());
        vacancy.setProject(project);
        vacancy.setName(createDto.name());
        vacancy.setDescription(createDto.description());
        vacancy.setPosition(createDto.position());
        vacancy.setCount(createDto.count());
        vacancy.setSalary(createDto.salary());
        vacancy.setWorkSchedule(createDto.workSchedule());
        vacancy.setCoverImageKey(createDto.coverImageKey());
        vacancy.setStatus(status);
        return vacancy;
    }

    public static VacancyDto toVacancyViewDto(Vacancy vacancy) {
        if (vacancy == null) {
            return null;
        }

        var candidates = toCandidateViewDtoList(vacancy.getCandidates());
        return new VacancyDto(vacancy.getId(),
                vacancy.getName(),
                vacancy.getDescription(),
                vacancy.getPosition(),
                vacancy.getProject().getId(),
                vacancy.getCreatedAt(),
                vacancy.getUpdatedAt(),
                candidates,
                vacancy.getStatus(),
                vacancy.getSalary(),
                vacancy.getCount(),
                vacancy.getWorkSchedule(),
                vacancy.getRequiredSkillIds(),
                vacancy.getCoverImageKey()
        );
    }

    public static List<CandidateDto> toCandidateViewDtoList(List<Candidate> candidates) {
        if (candidates == null) {
            return null;
        }
        List<CandidateDto> list = new ArrayList<CandidateDto>(candidates.size());
        for (Candidate candidate : candidates) {
            list.add(toCandidateDto(candidate));
        }
        return list;
    }

    public static CandidateDto toCandidateDto(Candidate candidate) {
        if (candidate == null) {
            return null;
        }
        Long vacancyId = candidateVacancyId(candidate);
        CandidateStatus status = candidate.getCandidateStatus();
        Long id = candidate.getId();
        Long userId = candidate.getUserId();
        String username = candidate.getUsername();
        String resumeDocKey = candidate.getResumeDocKey();
        String coverLetter = candidate.getCoverLetter();
        return new CandidateDto(id, userId, username, resumeDocKey, coverLetter, status, vacancyId);
    }

    private static Long candidateVacancyId(Candidate candidate) {
        if (candidate == null) {
            return null;
        }
        Vacancy vacancy = candidate.getVacancy();
        if (vacancy == null) {
            return null;
        }
        return vacancy.getId();
    }

    public static VacancyUpdateDto getUpdateDto(String name, String description, TeamRole position) {
        return new VacancyUpdateDto(
                "Analyst",
                "Analyst",
                TeamRole.ANALYST,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static Stream<Arguments> provideUpdateParams() {
        var projectId = 1L;
        var project = getProject(projectId, 2L, "Mega project");
        var userId = 1L;
        var createDto = getCreateDto(
                "Java dev",
                "strong java dev",
                TeamRole.DEVELOPER,
                projectId,
                WorkSchedule.REMOTE
        );
        var vacancyId = 1L;
        var updatedVacancy = toEntity(
                vacancyId,
                createDto,
                project,
                userId,
                VacancyStatus.OPEN
        );
        updatedVacancy.setName("Analyst");
        updatedVacancy.setDescription("Analyst");
        updatedVacancy.setPosition(TeamRole.ANALYST);
        updatedVacancy.setUpdatedBy(userId);
        updatedVacancy.setUpdatedAt(LocalDateTime.now());
        var updateDto = VacancyServiceTestData.getUpdateDto(
                "Analyst",
                "Analyst",
                TeamRole.ANALYST
        );
        var vacancyFromDb = toEntity(
                vacancyId,
                createDto,
                project,
                userId,
                VacancyStatus.OPEN
        );
        var expected = toVacancyViewDto(updatedVacancy);

        return Stream.of(
                Arguments.of(
                        userId,
                        updateDto,
                        project,
                        vacancyFromDb,
                        updatedVacancy,
                        expected
                )
        );
    }

    public static Stream<Arguments> provideGetListParams() {
        var filterDto = new VacancyFilterDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        var projectId = 1L;
        var project = getProject(projectId, 2L, "FAANG");
        Vacancy vacancy = Vacancy.builder()
                .id(1L)
                .name("Java Dev")
                .description("Spring project")
                .position(TeamRole.DEVELOPER)
                .status(VacancyStatus.OPEN)
                .salary(120000.0)
                .count(1)
                .project(project)
                .workSchedule(WorkSchedule.FULL_TIME)
                .requiredSkillIds(List.of(1L, 2L))
                .coverImageKey("img-key")
                .build();

        VacancyDto dto = toVacancyViewDto(vacancy);

        return Stream.of(
                Arguments.of(filterDto, List.of(vacancy), List.of(vacancy), List.of(dto))
        );
    }
}
