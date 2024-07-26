package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.apache.catalina.mapper.Mapper;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class MomentMapperTest {

    private MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private Moment ExpectedMoment;
    private MomentDto ExpectedMomentDto;
    private Project project;
    private List<Long> projectIds;
    private List<Long> userIds;
    private List<Project> projects;
    private TeamMember member;

    @BeforeEach
    public void setUp() {
        member = TeamMember.builder().id(3L).build();
        projectIds = new ArrayList<>();
        projectIds.add(1L);
        userIds = new ArrayList<>();
        userIds.add(2L);
        project = Project.builder().id(1L).build();
        projects = new ArrayList<>();
        projects.add(project);
        ExpectedMomentDto = MomentDto.builder()
                .name("test")
                .date(LocalDateTime.of(2021, 1, 1, 0, 0))
                .id(1L)
                .memberIds(List.of(3L))
                .projectIds(projectIds)
                .userIds(userIds)
                .build();
        ExpectedMoment = Moment.builder()
                .name("test")
                .date(LocalDateTime.of(2021, 1, 1, 0, 0))
                .id(1L)
                .projects(projects)
                .members(List.of(member))
                .userIds(userIds)
                .build();
    }

    @Test
    public void testEntityToDto() {
        Moment actualMoment = mapper.toEntity(ExpectedMomentDto);
        Assertions.assertEquals(actualMoment, ExpectedMoment);
    }

    @Test
    public void testDtoToEntity() {
        MomentDto actualMomentDto = mapper.toDto(ExpectedMoment);
        Assertions.assertEquals(actualMomentDto, ExpectedMomentDto);
    }

}
