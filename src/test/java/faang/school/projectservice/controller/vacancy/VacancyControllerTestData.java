package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.mapper.CandidateMapper;
import faang.school.projectservice.mapper.VacancyMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.model.WorkSchedule;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class VacancyControllerTestData {
    @Autowired
    private VacancyMapper mapper;

    public VacancyCreateDto getCreateDto() {
        return new VacancyCreateDto(
                "Java Dev",
                "we are looking for Java enjoyer",
                TeamRole.DEVELOPER,
                1L,
                1,
                300_000.0,
                WorkSchedule.REMOTE,
                List.of(1L, 2L, 3L, 4L, 5L),
                UUID.randomUUID().toString()
        );
    }

    public VacancyCreateDto getCreateDto(String name, String description, TeamRole position) {
        return new VacancyCreateDto(
                name,
                description,
                position,
                1L,
                1,
                300_000.0,
                WorkSchedule.REMOTE,
                List.of(1L, 2L, 3L, 4L, 5L),
                UUID.randomUUID().toString()
        );
    }

    public Vacancy getEntity() {
        var project = new Project();
        project.setId(1L);
        var entity = mapper.toEntity(getCreateDto());
        entity.setProject(project);
        entity.setId(1L);
        entity.setCreatedBy(1L);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus(VacancyStatus.OPEN);
        return entity;
    }

    public Vacancy getEntity(String name, String description, TeamRole position) {
        var project = new Project();
        project.setId(1L);
        var entity = mapper.toEntity(getCreateDto(name, description, position));
        entity.setProject(project);
        entity.setId(1L);
        entity.setCreatedBy(1L);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setStatus(VacancyStatus.OPEN);
        return entity;
    }

    public VacancyDto getViewDto() {
        return mapper.toViewDto(getEntity());
    }

    public VacancyDto getViewDto(String name, String description, TeamRole position) {
        return mapper.toViewDto(getEntity(name, description, position));
    }

    public VacancyUpdateDto getUpdateDto() {
        return new VacancyUpdateDto("updatedName",
                "updated description",
                TeamRole.DEVELOPER,
                VacancyStatus.POSTPONED,
                null,
                null,
                null,
                null,
                "updated cover image key"
        );
    }

    public List<VacancyDto> getList() {
        var viewDto = getViewDto();
        var viewDto2 = getViewDto("Manager", "Manager", TeamRole.MANAGER);
        var viewDto3 = getViewDto("Designer", "Designer", TeamRole.DESIGNER);
        var viewDto4 = getViewDto("Analyst", "Analyst", TeamRole.ANALYST);
        return List.of(viewDto, viewDto2, viewDto3, viewDto4);
    }

    public VacancyFilterDto getFilter() {
        return new VacancyFilterDto(
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
    }
}
