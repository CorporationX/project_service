package faang.school.projectservice;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ProjectTestConstants {
    public static final long OWNER_ID = 1L;

    public static final long NOT_OWNER_ID = 2L;

    public static final MultipartFile PROJECT_COVER_MULTIPART_FILE = new MultipartFile() {
        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getOriginalFilename() {
            return "";
        }

        @Override
        public String getContentType() {
            return "";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            return 0;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return new byte[0];
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {

        }
    };

    public static final long CHAIR_PROJECT_ID = 100L;

    public static final String PROJECT_COVER_IMAGE_ID = "folder/12345-image.jpeg";

    public static final InputStream PROJECT_COVER_INPUT_STREAM = new InputStream() {
        @Override
        public int read() throws IOException {
            return 0;
        }
    };

    public static final MockMultipartFile PROJECT_COVER_MOCK_MULTIPART_FILE = new MockMultipartFile("file",
            "cover.jpg", "image/jpeg", "test image content".getBytes());
}
