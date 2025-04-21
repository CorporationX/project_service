package faang.school.projectservice.constants;

import java.math.BigInteger;

public class Constants {
    public static final BigInteger BASE_MAX_STORAGE_BYTES = BigInteger.valueOf(2L * 1024 * 1024 * 1024);
    public static final BigInteger SUBSCRIBE_MAX_STORAGE_BYTES = BigInteger.valueOf(10L * 1024 * 1024 * 1024);
    public static final String PROJECT_NOT_FOUND = "Project not found.";
    public static final String STORAGE_LIMIT_EXCEEDED = "Project storage limit exceeded.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String RESOURCE_NOT_FOUND = "Resource not found.";
    public static final String NO_PERMISSION = "You don't have permission to delete this file.";
    public static final String BUCKET_FAIL = "Failed to created/check bucket.";
    public static final String UPLOAD_FAIL = "Failed to upload file to MinIO.";
    public static final String DELETE_FAIL = "Failed to delete file from MinIO.";
    public static final String USER_NOT_MEMBER = "User is not a member of the project.";
}
