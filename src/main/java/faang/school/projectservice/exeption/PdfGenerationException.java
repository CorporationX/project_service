package faang.school.projectservice.exeption;

import org.springframework.http.HttpStatus;

public class PdfGenerationException extends ApiException {

    public PdfGenerationException(String message) {
        super(message, message);
    }

    @Override
    protected HttpStatus getDefaultStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
