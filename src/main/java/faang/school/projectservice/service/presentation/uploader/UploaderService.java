package faang.school.projectservice.service.presentation.uploader;

import faang.school.projectservice.model.Project;

import java.io.File;

public interface UploaderService {
    void uploadPdf(Project project, File pdfFile, String fileKey);
}
