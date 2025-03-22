package faang.school.projectservice.exception;

public class CampaignNotFoundException extends CustomException {

    public CampaignNotFoundException(Long id) {
        super(ExceptionMessage.CAMPAIGN_NOT_FOUND, id);
    }
}
