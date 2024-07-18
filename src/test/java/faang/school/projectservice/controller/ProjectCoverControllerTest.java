package faang.school.projectservice.controller;

import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.service.project.ProjectCoverServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectCoverControllerTest {
    @Mock
    private ProjectCoverServiceImpl projectCoverService;
    @InjectMocks
    private ProjectCoverController controller;
    private MockMvc mockMvc;


    private MockMultipartFile file;
    private final long projectId = 1L;
    private final String coverImageId = String.format("project/cover/%d", projectId);
    private ProjectCoverDto projectCoverDto;

    @BeforeEach
    public void setUp() {

        file = new MockMultipartFile("name", "original_name", "IMAGE", new byte[2_097_152]);
        long userId = 3L;

        projectCoverDto = ProjectCoverDto.builder()
                .id(projectId)
                .ownerId(userId)
                .coverImageId(coverImageId)
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testSave() throws Exception {
        when(projectCoverService.save(eq(projectId), any(MultipartFile.class)))
                .thenReturn(projectCoverDto);

        mockMvc.perform(multipart("/projects/cover/" + projectId)
                        .file("file", file.getBytes())
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(projectCoverDto.getId()))
                .andExpect(jsonPath("$.ownerId").value(projectCoverDto.getOwnerId()))
                .andExpect(jsonPath("$.coverImageId").value(projectCoverDto.getCoverImageId()));

        verify(projectCoverService, times(1)).save(eq(projectId), any(MultipartFile.class));

        verifyNoMoreInteractions(projectCoverService);
    }

    @Test
    void testGetCover() throws Exception {
        when(projectCoverService.getByProjectId(projectId))
                .thenReturn(new InputStreamResource(file.getInputStream()));

        mockMvc.perform(get("/projects/cover/" + projectId)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        verify(projectCoverService, times(1)).getByProjectId(projectId);
        verifyNoMoreInteractions(projectCoverService);
    }

    @Test
    void deleteProjectCover() throws Exception {
        projectCoverDto.setCoverImageId(null);
        when(projectCoverService.deleteByProjectId(eq(projectId)))
                .thenReturn(projectCoverDto);

        mockMvc.perform(delete("/projects/cover/" + projectId)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectCoverDto.getId()))
                .andExpect(jsonPath("$.ownerId").value(projectCoverDto.getOwnerId()))
                .andExpect(jsonPath("$.coverImageId").value(projectCoverDto.getCoverImageId()));

        verify(projectCoverService, times(1))
                .deleteByProjectId(eq(projectId));

        verifyNoMoreInteractions(projectCoverService);
    }
}
