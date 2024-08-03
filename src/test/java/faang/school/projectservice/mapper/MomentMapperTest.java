package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentMapperTest {

    private MomentMapperImpl mapper;
    private Moment moment;
    private MomentDto momentDto;
    private Project project;
    private List<Long> projectIds;
    private List<Long> userIds;
    private List<Project> projects;

    @BeforeEach
    public void setUp() {
        mapper = new MomentMapperImpl();
        projectIds = new ArrayList<>();
        projectIds.add(1L);
        userIds = new ArrayList<>();
        userIds.add(2L);
        project = Project.builder().id(1L).build();
        projects = new ArrayList<>();
        projects.add(project);
        momentDto = momentDto.builder().name("test")
                .date(LocalDateTime.of(2021, 1, 1, 0, 0))
                .id(1L).projectIds(projectIds).userIds(userIds).build();
        moment = moment.builder().name("test")
                .date(LocalDateTime.of(2021, 1, 1, 0, 0))
                .id(1L).projects(projects).userIds(userIds).build();
    }

    @Test
    public void testEntityToDto() {
        Moment actualMoment = mapper.toEntity(momentDto);
        Assertions.assertEquals(actualMoment, moment);
    }

    @Test
    public void testDtoToEntity() {
        MomentDto actualMomentDto = mapper.toDto(moment);
        Assertions.assertEquals(actualMomentDto, momentDto);
    }

    @Test
    public void testUpdate() {
        MomentDto newMomentDto = MomentDto.builder().id(3L).name("newName").
                date(LocalDateTime.of(2022, 1, 1, 0, 0))
                .projectIds(List.of(2L, 3L)).userIds(List.of(2L, 3L)).build();
        mapper.update(newMomentDto, moment);
        Assertions.assertEquals("newName", moment.getName());
        Assertions.assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0), moment.getDate());
        Assertions.assertEquals(3L, moment.getId());
        Assertions.assertEquals(List.of(2L, 3L), moment.getProjects().stream().map(Project::getId).toList());
        Assertions.assertEquals(List.of(2L, 3L), moment.getUserIds());
    }

}
