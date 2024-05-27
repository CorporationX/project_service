package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.resource.ProjectResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectResourceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectResourceService projectResourceService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProjectResourceController controller;

    private final long projectId = 1L;
    private final long resourceId = 2L;
    private final long userId = 3L;
    private ResourceDto resourceDto;
    private MockMultipartFile file;

    @BeforeEach
    public void setUp() {

        file = new MockMultipartFile("name", "original_name", "TEXT", new byte[10000]);

        Project project = Project.builder()
                .id(projectId)
                .storageSize(new BigInteger("1", 32))
                .build();

        TeamMember teamMember = TeamMember.builder()
                .id(4L)
                .userId(userId)
                .team(Team.builder().project(project).build())
                .roles(List.of(TeamRole.INTERN))
                .build();

        resourceDto = ResourceDto.builder()
                .id(resourceId)
                .key("project/" + projectId + "/" + resourceId)
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(teamMember.getRoles())
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdById(teamMember.getId())
                .updatedById(teamMember.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .projectId(project.getId())
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void saveFile() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        when(projectResourceService.saveFile(eq(userId), eq(projectId), any(MultipartFile.class))).thenReturn(resourceDto);

        mockMvc.perform(multipart("/projects/" + projectId + "/resources")
                        .file("file", file.getBytes())
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(resourceDto.getId()));

        InOrder inOrder = inOrder(userContext, projectResourceService);
        inOrder.verify(userContext, times(1)).getUserId();
        inOrder.verify(projectResourceService, times(1)).saveFile(eq(userId), eq(projectId), any(MultipartFile.class));
    }

    @Test
    void getFile() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        when(projectResourceService.getFile(userId, projectId, resourceId)).thenReturn(new InputStreamResource(file.getInputStream()));

        mockMvc.perform(get("/projects/" + projectId + "/resources/" + resourceId))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(userContext, projectResourceService);
        inOrder.verify(userContext, times(1)).getUserId();
        inOrder.verify(projectResourceService, times(1)).getFile(userId, projectId, resourceId);
    }

    @Test
    void deleteFile() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);

        mockMvc.perform(delete("/projects/" + projectId + "/resources/" + resourceId))
                .andExpect(status().isOk());

        InOrder inOrder = inOrder(userContext, projectResourceService);
        inOrder.verify(userContext, times(1)).getUserId();
        inOrder.verify(projectResourceService, times(1)).deleteFile(userId, projectId, resourceId);
    }
}