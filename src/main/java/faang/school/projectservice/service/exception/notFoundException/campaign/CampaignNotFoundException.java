package faang.school.projectservice.service.exception.notFoundException.campaign;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class CampaignNotFoundException extends EntityNotFoundException {
    public CampaignNotFoundException(String message) {
        super(message);
    }
}