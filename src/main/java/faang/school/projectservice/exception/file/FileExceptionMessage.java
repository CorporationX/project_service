package faang.school.projectservice.exception.file;

import lombok.Getter;

@Getter
public enum FileExceptionMessage {
    CONVERTING_INPUT_STREAM_TO_COVER("IO exception while converting input stream to cover"),
    CONVERTING_IMAGE_TO_INPUT_STREAM("IO exception while converting image to input stream"),
    CALCULATE_IMAGE_SIZE("IO exception while calculating image size");
    
    private final String message;
    
    FileExceptionMessage(String message) {
        this.message = message;
    }
}
