package faang.school.projectservice.exception.donation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DonationExceptionMessage {

    public static final String NOT_FOUND = "Donation not found";
    public static final String EXCEED_AMOUNT = "The donation amount exceeds the amount required to achieve the goal";
}
