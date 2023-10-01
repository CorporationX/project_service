package faang.school.projectservice.exception.notFoundException.donation;

import faang.school.projectservice.exception.notFoundException.EntityNotFoundException;

public class DonationNotFoundException extends EntityNotFoundException {
    public DonationNotFoundException(String message) {
        super(message);
    }
}