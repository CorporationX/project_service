package faang.school.projectservice.exception.notFoundException.campaign;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class CampaignNotFoundException extends EntityNotFoundException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}