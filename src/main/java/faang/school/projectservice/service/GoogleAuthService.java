package faang.school.projectservice.service;

import java.io.IOException;

public interface GoogleAuthService {
    String getAccessToken() throws IOException;
}