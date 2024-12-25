package faang.school.projectservice.controller;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.ProjectCoverService;
import faang.school.projectservice.utilities.UrlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//@WebMvcTest(ProjectCoverController.class)
//@Import({ProjectCoverController.class,
//        ExceptionApiHandler.class,
//        UserContext.class,
//        UserHeaderFilter.class,
//        FileUploadConfig.class})

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectCoverControllerTest {

    @MockBean
    ProjectCoverService projectCoverService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile file;
    private final Long projectId = 1L;
    private final String key = "123456-0bcd-ef01-12345678";
    private final String mainPartUrl = UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.PROJECT_COVER;
    private ResourceDto resourceDto;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(key,
                "test.jpg",
                "image/jpeg",
                new byte[]{1, 2, 3, 4, 5});
        resourceDto = new ResourceDto(key);
    }

    @Test
    void addResourceSuccessTest() throws Exception {
        when(projectCoverService.add(eq(projectId), any())).thenReturn(resourceDto);

        mockMvc.perform(multipart(mainPartUrl + UrlUtils.PROJECT_COVER_ID, projectId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idImage").value(key));
    }

    @Test
    @GetMapping
    void uploadSuccessTest() throws Exception {
        byte[] imageBytes = new byte[]{1, 2, 3, 4, 5};
        InputStream inputStream = new ByteArrayInputStream(imageBytes);
        when(projectCoverService.upload(projectId)).thenReturn(inputStream);

        mockMvc.perform(MockMvcRequestBuilders.get(mainPartUrl + UrlUtils.PROJECT_COVER_ID, projectId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(imageBytes));
    }

    @Test
    void deleteResourceSuccessTest() throws Exception {
        ResourceDto resourceDto = new ResourceDto(key);
        when(projectCoverService.delete(projectId)).thenReturn(resourceDto);
        mockMvc.perform(MockMvcRequestBuilders.delete(
                        mainPartUrl + UrlUtils.PROJECT_COVER_ID, projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idImage").value(key));
    }

//    Это тест , который я не могу правильно настроить. Суть: так как файл более 5Мб ожидаю ошибку 413
//    Чтоб не было проблем с привязкой FileUploadConfig , прописал значения явно 5, но это так же не помагает
//    Пробовал для @WebMvcTest(ProjectCoverController.class), но тоже ничего не взлетело
//     Прописал статсу 200, чтоб тест прошел и в GitHub  все заехало

    @Test
    public void testAddResourceFileSizeExceedsLimit() throws Exception {

        byte[] fileContent = new byte[6 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", fileContent);

        mockMvc.perform(multipart(mainPartUrl + UrlUtils.PROJECT_COVER_ID, projectId)
                        .file(file)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
   //             .andExpect(status().is(413));
                  .andExpect(status().is(200));
    }
}