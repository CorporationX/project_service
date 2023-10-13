package faang.school.projectservice.service.exception.notFoundException.donation;

import faang.school.projectservice.service.exception.notFoundException.EntityNotFoundException;

public class DonationNotFoundException extends EntityNotFoundException {
    public DonationNotFoundException(String message) {
        super(message);
    }
}