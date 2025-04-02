package faang.school.projectservice.exception;

public class CampaignNotActiveException extends RuntimeException {

    public CampaignNotActiveException(String message, Object... args) {
        super(String.format(message, args));
    }
}
