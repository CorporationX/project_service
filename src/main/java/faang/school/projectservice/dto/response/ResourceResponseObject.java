package faang.school.projectservice.dto.response;

import java.io.InputStream;

public record ResourceResponseObject(InputStream inputStream, String contentType) { }
