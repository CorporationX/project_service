package faang.school.projectservice.controller.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.image.ImageService;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validator.project.ProjectValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    private ProjectController projectController;
    @Mock
    private ProjectService projectService;
    @Mock
    private ImageService imageService;
    private ProjectValidator projectValidator;
    private ProjectRepository projectRepository;
    private UserContext userContext;
    @Mock
    private S3Service s3Service;
    private final long ownerId = 1L;
    private final String name = "Project";
    private final String description = "Cool project";
    private final long projectId = 10L;
    private final ProjectVisibility visibility = ProjectVisibility.PUBLIC;
    private ProjectDto projectDto;
    private ProjectFilterDto filters;
    private MockMvc mockMvc;
    private final String userId = "3";

    @BeforeEach
    void setUp() {
        projectValidator = Mockito.spy(new ProjectValidator(projectRepository, userContext));
        projectController = new ProjectController(projectService, projectValidator, imageService, s3Service);

        projectDto = ProjectDto.builder()
                .id(projectId)
                .name(name)
                .description(description)
                .ownerId(ownerId)
                .visibility(visibility)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }

    @Test
    public void testAddInvalidName() {
        String blank = "  ";
        RuntimeException e;

        projectDto.setName(blank);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.add(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid name " + projectDto.getName());

        projectDto.setName(null);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.add(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid name " + projectDto.getName());
    }

    @Test
    public void testAddInvalidDescription() {
        String blank = "  ";
        RuntimeException e;

        projectDto.setDescription(blank);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.add(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid description " + projectDto.getDescription());

        projectDto.setDescription(null);
        e = Assertions.assertThrows(RuntimeException.class, () -> projectController.add(userId, projectDto));
        Assertions.assertEquals(e.getMessage(), "Invalid description " + projectDto.getDescription());
    }

    @Test
    public void testAdd() throws Exception {
        String jsonRequest = new ObjectMapper().writeValueAsString(projectDto);
        when(projectService.add(projectDto)).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/project/add")
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectDto.getName()));

        verify(projectService).add(projectDto);
    }

    @Test
    public void testUpdateInvalidArguments() {
        long id = 0L;
        projectDto.setId(id);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.update(projectDto));

        projectDto.setId(null);
        Assertions.assertThrows(RuntimeException.class, () -> projectController.update(projectDto));
    }

    @Test
    public void testUpdate() throws Exception {
        String jsonRequest = new ObjectMapper().writeValueAsString(projectDto);
        when(projectService.update(projectDto)).thenReturn(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/project")
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectDto.getName()));

        verify(projectService).update(projectDto);
    }

    @Test
    public void testGetProjectsWithFilters() throws Exception {
        filters = new ProjectFilterDto("name", ProjectStatus.CREATED);
        when(projectService.getProjectsWithFilters(filters)).thenReturn(List.of(projectDto));
        String jsonRequest = new ObjectMapper().writeValueAsString(filters);

        mockMvc.perform(MockMvcRequestBuilders.post("/project/getByFilters")
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value(projectDto.getName()));

        verify(projectService).getProjectsWithFilters(filters);
    }

    @Test
    public void testGetAllProjects() {
        when(projectService.getAllProjects()).thenReturn(List.of(projectDto));
        projectController.getAllProjects();
        verify(projectService).getAllProjects();
    }

    @Test
    public void testGetProjectByIdWithWrongId() {
        Long idZero = 0L;
        Assertions.assertThrows(RuntimeException.class, () -> projectController.getProjectById("3", idZero));
        Assertions.assertThrows(RuntimeException.class, () -> projectController.getProjectById("3", null));
    }

    @Test
    public void testGetProjectById() throws Exception {
        when(projectService.getProjectById(projectId)).thenReturn(projectDto);
        String jsonRequest = new ObjectMapper().writeValueAsString(projectDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/project/" + projectId)
                        .header("x-user-id", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(projectDto.getName()));

        verify(projectService).getProjectById(projectId);
    }

    @Test
    public void testAddCoverImage() throws Exception {
        String result = "Success";
        MockMultipartFile coverImage = new MockMultipartFile(
                "coverImage", "filename.jpg", "image/jpeg", description.getBytes());
        doNothing().when(projectValidator).validateCover(coverImage);
        when(imageService.resizeImage(coverImage)).thenReturn(coverImage);
        when(s3Service.addCover(projectId, coverImage)).thenReturn(result);


        mockMvc.perform(MockMvcRequestBuilders.multipart("/project/" + projectId + "/cover")
                        .file(coverImage)
                        .header("x-user-id", "3")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string(result));

        verify(projectValidator).validateCover(coverImage);
        verify(imageService).resizeImage(coverImage);
        verify(s3Service).addCover(projectId, coverImage);
    }
}