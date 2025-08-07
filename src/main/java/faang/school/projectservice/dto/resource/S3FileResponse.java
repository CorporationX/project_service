package faang.school.projectservice.dto.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.InputStreamResource;

@Schema(description = "Response object containing a downloadable file from S3")
public record S3FileResponse(

        @Schema(description = "Name of the file")
        String fileName,

        @Schema(description = "File input stream resource")
        InputStreamResource inputStream,

        @Schema(description = "MIME content type of the file")
        String contentType
) {
}
