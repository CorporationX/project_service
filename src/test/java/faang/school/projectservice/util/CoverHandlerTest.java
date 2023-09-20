package faang.school.projectservice.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class CoverHandlerTest {
    @InjectMocks
    private CoverHandler coverHandler;
    @Mock
    private MultipartFile multipartFileMock;

    @BeforeEach
    void setUp() {
    }


    @Test
    void resizeCover() {


    }

    @Test
    void getNewWidth() {
    }

    @Test
    void getNewHeight() {
    }
}