package faang.school.projectservice.exception;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}
