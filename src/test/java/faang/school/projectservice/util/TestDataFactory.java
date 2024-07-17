package faang.school.projectservice.util;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.List.of;

@UtilityClass
public final class TestDataFactory {

    public static MomentDto createMomentDto(){
        MomentDto momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setName("First Moment");
        momentDto.setDescription("Description of first moment");
        momentDto.setDate(LocalDateTime.now());
        momentDto.setProjects(new ArrayList<>());
        momentDto.setUserIds(new ArrayList<>(of(1L, 2L, 3L)));
        momentDto.setCreatedAt(LocalDateTime.now());
        momentDto.setUpdatedAt(LocalDateTime.now());
        momentDto.setCreatedBy(1L);
        momentDto.setUpdatedBy(1L);
        return momentDto;
    }

    public static Moment createMoment(){
        Moment moment = new Moment();
        moment.setId(1L);
        moment.setName("First Moment");
        moment.setDescription("Description of first moment");
        moment.setDate(LocalDateTime.now());
        moment.setProjects(new ArrayList<>());
        moment.setUserIds(new ArrayList<>(of(1L, 2L, 3L)));
        moment.setCreatedAt(LocalDateTime.of(2019, 3, 28, 14, 33, 48, 640000));
        moment.setUpdatedAt(LocalDateTime.now());
        moment.setCreatedBy(1L);
        moment.setUpdatedBy(1L);
        return moment;
    }


    public static List<Moment> createMomentList(){
        return of(createMoment());
    }

    public static List<MomentDto> createMomentDtoList(){
        return of(createMomentDto());
    }

    public static Project createProject(){
        Project project = new Project();
        project.setId(1L);
        project.setName("Project Name");
        project.setDescription("Project Description");
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setMoments(new ArrayList<>());

        return project;
    }

    public static ProjectDto createProjectDto(){
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setName("Project Name");
        projectDto.setDescription("Project Description");
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setUpdatedAt(LocalDateTime.now());
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);
        projectDto.setMoments(new ArrayList<>());

        return projectDto;
    }

    public static MomentFilterDto createMomentFilterDto(){
        return MomentFilterDto.builder()
                .namePattern("First Moment")
                .createdAt(LocalDateTime.of(2019, 3, 28, 14, 33, 48, 640000))
                .build();
    }
}
