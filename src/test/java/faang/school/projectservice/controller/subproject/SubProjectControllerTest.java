package faang.school.projectservice.controller.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SubProjectControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    private ProjectService projectService;
    private Long parentProjectId = 1L;
    private CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
            .parentProjectId(parentProjectId)
            .name("SubProject name")
            .description("SubProject description")
            .build();

    private HttpHeaders headers = new HttpHeaders();
    private Long xUserId = 12345L;
    private String webApiUrl;

    @BeforeEach
    void setUp() {
        headers.add("x-user-id", xUserId.toString());
        webApiUrl = "http://localhost:" + port + "/api/v1";
    }

    @Test
    public void testCreateSubProjectInvalidData() {
        CreateSubProjectDto subProjectDtoBad = new CreateSubProjectDto();
        HttpEntity<CreateSubProjectDto> requestBad = new HttpEntity<>(subProjectDtoBad, headers);

        ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                webApiUrl + "/subProjects",
                HttpMethod.POST,
                requestBad,
                new ParameterizedTypeReference<Map<String, String>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKeys("name", "description", "parentProjectId");
        assertThat(response.getBody().get("name"))
                .asString().contains("Project name can't be null or empty");
        assertThat(response.getBody().get("description"))
                .asString().contains("Project description can't be null or empty");
        assertThat(response.getBody().get("parentProjectId"))
                .asString().contains("Parent Project ID can't be null");

        verify(projectService, times(0)).createSubProject(subProjectDto);
    }

    @Test
    public void testCreateSubprojectValidData() {
        HttpEntity<CreateSubProjectDto> request = new HttpEntity<>(subProjectDto, headers);
        ProjectDto expectedProject = new ProjectDto();

        when(projectService.createSubProject(subProjectDto)).thenReturn(expectedProject);

        ResponseEntity<ProjectDto> response = restTemplate.exchange(
                webApiUrl + "/subProjects",
                HttpMethod.POST,
                request,
                ProjectDto.class
        );

        assertThat(response.getBody()).isEqualTo(expectedProject);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void testCreateSubProjectDtoNull() {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateSubProjectDto> requestBad = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                webApiUrl + "/subProjects",
                HttpMethod.POST,
                requestBad,
                String.class
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(projectService, times(0)).createSubProject(any());
    }

    @Test
    public void testUpdateSubProject() {
        UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        HttpEntity<UpdateSubProjectDto> request = new HttpEntity<>(updateDto, headers);
        ProjectDto expectedProject = new ProjectDto();

        when(projectService.updateSubProject(parentProjectId, updateDto)).thenReturn(expectedProject);

        ProjectDto response = restTemplate.exchange(
                webApiUrl + "/subProjects/1",
                HttpMethod.PUT,
                request,
                ProjectDto.class
        ).getBody();

        assertThat(response).isEqualTo(expectedProject);
    }

    @Test
    public void testGetFilteredSubProjects() {
        FilterSubProjectDto filterDto = new FilterSubProjectDto();
        HttpEntity<FilterSubProjectDto> request = new HttpEntity<>(filterDto, headers);

        List<ProjectDto> expected = List.of(new ProjectDto());

        when(projectService.getFilteredSubProjects(parentProjectId, filterDto)).thenReturn(expected);

        ResponseEntity<List<ProjectDto>> response = restTemplate.exchange(
                webApiUrl + "/" + parentProjectId + "/subProjects",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<List<ProjectDto>>() {}
        );

        assertThat(response.getBody()).isEqualTo(expected);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
