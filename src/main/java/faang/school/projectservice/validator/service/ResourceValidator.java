package faang.school.projectservice.validator.service;

import faang.school.projectservice.exception.resource.DataValidationException;
import faang.school.projectservice.model.Project;

import java.math.BigInteger;

public class ResourceValidator {

    public void checkStorageSize(Project project) {
        BigInteger storageSize = project.getStorageSize();
        BigInteger limit = BigInteger.valueOf(2097152);
        if (storageSize.compareTo(limit) > 0) {
            throw new DataValidationException("File over limit");
        }
    }
}
