package faang.school.projectservice.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.ProjectMapperImpl;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.mapper.TeamMapperImpl;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.mapper.TeamMemberMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UpdateSubProjectStatusTest {
    @Mock
    private ProjectMapper mapper;
    @Mock
    private TeamMapper teamMapper;
    @Mock
    private TeamMemberMapper teamMemberMapper;
    @Mock
    private MomentRepository momentRepository;
    @InjectMocks
    private UpdateSubProjectStatus updateSubProjectStatus;
    private UpdateSubProjectDto paramDto;
    private ProjectDto projectDto;
    @Captor
    private ArgumentCaptor<Moment> momentCaptor;

    @BeforeEach
    public void setup() {
        paramDto = new UpdateSubProjectDto();
        projectDto = new ProjectDto();
    }

    @Test
    public void testIsExecutable_statusNull() {
        boolean expected = false;

        boolean result = updateSubProjectStatus.isExecutable(paramDto);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testIsExecute_statusNotNull() {
        boolean expected = true;
        paramDto.setStatus(ProjectStatus.CREATED);

        boolean result = updateSubProjectStatus.isExecutable(paramDto);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testExecute_NotEqualChildrenStatus() {
        ProjectDto childOne = new ProjectDto();
        childOne.setStatus(ProjectStatus.CREATED);
        ProjectDto child2 = new ProjectDto();
        child2.setStatus(ProjectStatus.COMPLETED);
        projectDto.setChildren(new ArrayList<>(List.of(childOne, child2)));
        paramDto.setStatus(ProjectStatus.COMPLETED);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            updateSubProjectStatus.execute(projectDto, paramDto);
        });
        Assertions.assertEquals("Не все подпроекты имеют указанный статус", exception.getMessage());
    }
}
