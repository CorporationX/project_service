package faang.school.projectservice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NoAccessExceptionMessage {
    DOWNLOAD_PERMISSION_ERROR("Only members of this project can download files from storage."),
    DELETE_PERMISSION_ERROR("Only uploader, manager or owner of this project can delete files from storage.");

    private final String message;
}
