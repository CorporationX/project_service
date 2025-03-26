package faang.school.projectservice.exception;

public class CampaignNotFoundException extends CustomException {

    public CampaignNotFoundException(ExceptionMessage message, Long id) {
        super(message, id);
    }
}
