package faang.school.projectservice.controller.coverimage;

import faang.school.projectservice.service.coverimage.CoverImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CoverImageControllerTest {

    @InjectMocks
    private CoverImageController coverImageController;

    @Mock
    private CoverImageService coverImageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(coverImageController).build();
    }

    @Test
    void testCreate() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "Test Image Content".getBytes());
        doNothing().when(coverImageService).create(1L, multipartFile);

        mockMvc.perform(multipart("/cover-image/1")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete() {
        doNothing().when(coverImageService).delete(1L);

        coverImageController.delete(1L);

        verify(coverImageService, times(1)).delete(1L);
    }
}