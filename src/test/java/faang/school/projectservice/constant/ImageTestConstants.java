package faang.school.projectservice.constant;

import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class ImageTestConstants {
    public static final InputStream IMAGE_INPUT_STREAM
            = ProjectTestConstants.class.getResourceAsStream("/images/TEST_IMAGE.jpg");

    public static final MockMultipartFile IMAGE_MOCK_MULTIPART_FILE;

    static {
        try {
            IMAGE_MOCK_MULTIPART_FILE = new MockMultipartFile("file",
                    "TEST_IMAGE.jpg", "image/jpg", IMAGE_INPUT_STREAM);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
