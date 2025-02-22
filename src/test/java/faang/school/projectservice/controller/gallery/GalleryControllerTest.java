package faang.school.projectservice.controller.gallery;

import faang.school.projectservice.dto.gallery.GalleryResponseDto;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.gallery.GalleryService;
import faang.school.projectservice.util.GalleryDataUtilTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GalleryControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GalleryService galleryService;
    @MockBean
    private ProjectRepository projectRepository;
    @Value("${spring.servlet.mvc.path}/gallery")
    private String mvcPath;

    @Test
    public void testUploadFilesValid() throws Exception {
        GalleryResponseDto expectedGallery = GalleryDataUtilTest.getGalleryResponseDto();
        Project expectedProject = Project.builder().id(1L).build();

        when(galleryService.uploadFiles(any(Long.class), any(List.class))).thenReturn(expectedGallery);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.ofNullable(expectedProject));


        List<MockMultipartFile> files = GalleryDataUtilTest.getMultipartFiles();

        mockMvc.perform(multipart(mvcPath + "/project/1/upload")
                        .file(files.get(0))
                        .file(files.get(1))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.keys", hasSize(2)));

    }

    @Test
    public void testUploadFilesStatusNotFoundValid() throws Exception {
        when(galleryService.uploadFiles(any(Long.class), any(List.class)))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        MockMultipartFile file1 = new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.txt", "text/plain", "File 2 content".getBytes());

        mockMvc.perform(multipart(mvcPath + "/project/2/upload")
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteFilesValid() throws Exception {
        doNothing().when(galleryService).deleteFiles(List.of("key1", "key2"));

        mockMvc.perform(delete(mvcPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\n" +
                                "  \"keys\": [\"key1\", \"key2\"]\n" +
                                "}"))
                .andExpect(status().isNoContent());

    }

    @Test
    public void testDownloadFilesValid() throws Exception {
        List<String> base64Images = List.of(
                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP...",
                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP..."
        );

        when(galleryService.downloadImagesAsBase64(anyLong())).thenReturn(base64Images);

        mockMvc.perform(post(mvcPath + "/project/1/download")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP...\"," +
                        "\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP...\"]"));
    }

    @Test
    public void testDownloadFilesStatusNotFoundValid() throws Exception {
        List<String> base64Images = List.of(
                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP...",
                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP..."
        );

        when(galleryService.downloadImagesAsBase64(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(post(mvcPath + "/project/1/download")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

}
