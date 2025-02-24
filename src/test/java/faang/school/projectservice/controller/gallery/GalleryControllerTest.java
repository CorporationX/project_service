package faang.school.projectservice.controller.gallery;

import faang.school.projectservice.dto.gallery.GalleryResponseDto;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.gallery.GalleryService;
import faang.school.projectservice.util.GalleryDataUtilTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"spring.servlet.mvc.path=/api/v1"})
public class GalleryControllerTest {

    private static final String BASE_URL = "/api/v1/gallery";

    private MockMvc mockMvc;

    @Mock
    private GalleryService galleryService;

    @InjectMocks
    private GalleryController galleryController;

    @BeforeEach
    void setUp() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Properties properties = new Properties();
        properties.setProperty("spring.servlet.mvc.path", "/api/v1");
        configurer.setProperties(properties);

        mockMvc = MockMvcBuilders.standaloneSetup(galleryController)
                .addPlaceholderValue("spring.servlet.mvc.path", "/api/v1")
                .build();
    }

    @Test
    public void testUploadFilesValid() throws Exception {
        GalleryResponseDto expectedGallery = GalleryDataUtilTest.getGalleryResponseDto();
        Project expectedProject = Project.builder().id(1L).build();

        when(galleryService.uploadFiles(any(Long.class), any(List.class))).thenReturn(expectedGallery);


        List<MockMultipartFile> files = GalleryDataUtilTest.getMultipartFiles();

        mockMvc.perform(multipart(BASE_URL + "/project/1/upload")
                        .file(files.get(0))
                        .file(files.get(1))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1))
                .andExpect(jsonPath("$.keys", hasSize(2)));

    }

    @Disabled
    @Test
    public void testUploadFilesStatusNotFoundValid() throws Exception {
        when(galleryService.uploadFiles(any(Long.class), any(List.class)))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        MockMultipartFile file1 = new MockMultipartFile("files", "file1.txt", "text/plain", "File 1 content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.txt", "text/plain", "File 2 content".getBytes());

        mockMvc.perform(multipart(BASE_URL + "/project/2/upload")
                        .file(file1)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteFilesValid() throws Exception {
        doNothing().when(galleryService).deleteFiles(List.of("key1", "key2"));

        mockMvc.perform(delete(BASE_URL)
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

        mockMvc.perform(post(BASE_URL + "/project/1/download")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP...\"," +
                        "\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP...\"]"));
    }

    @Disabled
    @Test
    public void testDownloadFilesStatusNotFoundValid() throws Exception {
        List<String> base64Images = List.of(
                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP...",
                "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAP..."
        );

        when(galleryService.downloadImagesAsBase64(any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(post(BASE_URL + "/project/1/download")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

}
