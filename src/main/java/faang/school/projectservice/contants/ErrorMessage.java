package faang.school.projectservice.contants;

public class ErrorMessage {
    public static final String ERROR_INVALID_FILE_TYPE = "Invalid file type\n";
    public static final String ERROR_FILE_EXCEPTION = "Failed to read file input stream\n";
    public static final String ERROR_UNAUTHORIZED_ACCESS = "Only the team manager can delete the avatar.\n";
    public static final String ERROR_COMPRESS_IMAGE = "Failed to compress image: {}\n";

    public static String getErrorLimitSizeFile(long maxLimitSize){
        return String.format(ERROR_LIMIT_SIZE_FILE, maxLimitSize);
    }

    public static String getErrorNotFoundTeam(long id) {
        return String.format(ERROR_NOT_FOUND_TEAM, id);
    }

    private static final String ERROR_LIMIT_SIZE_FILE = "File size exceeds the maximum allowed limit of %d\n";
    private static final String ERROR_NOT_FOUND_TEAM = "Not found team ID: %d\n";
}
