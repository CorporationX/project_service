package faang.school.projectservice.model;

import java.io.InputStream;

public record ResourceWithFileStream(Resource resource, InputStream inputStream) {
}
