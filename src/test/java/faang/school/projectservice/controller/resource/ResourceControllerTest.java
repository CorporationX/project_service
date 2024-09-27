package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ResourceController.class)
@RequiredArgsConstructor
class ResourceControllerTest {
    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceController resourceController;

    @Autowired
    private  MockMvc mockMvc;

    @Test
    void testUploadFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",                     // Имя параметра
                "testfile.txt",              // Имя файла
                "text/plain",                // Тип файла
                "Hello, World!".getBytes()   // Контент файла
        );

        // Мокаем результат работы сервиса
        ResourceDto mockResourceDto = new ResourceDto();
        when(resourceService.addResource(anyLong(), any(MultipartFile.class)))
                .thenReturn(mockResourceDto);

        // Выполняем multipart-запрос
        mockMvc.perform(MockMvcRequestBuilders.multipart("/{projectId}", 1L)
                        .file(file) // Добавляем файл в запрос
                        .header("x-user-id", 123L) // Добавляем заголовок
                        .contentType(MediaType.MULTIPART_FORM_DATA)) // Указываем тип контента
                .andExpect(status().isOk()) // Ожидаем статус 200 OK
                .andExpect(jsonPath("$.id").exists()); // Проверяем, что в ответе есть поле id

        // Проверяем, что метод сервиса был вызван один раз с нужными параметрами
        verify(resourceService, times(1)).addResource(eq(1L), any(MultipartFile.class));
    }

    @Test
    void testUpdateFile() {
    }

    @Test
    void testDeleteFile() {
    }

    @Test
    void testDownloadFile() {
    }


}