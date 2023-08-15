package faang.school.projectservice.validator.offer;

import faang.school.projectservice.dto.offer.OfferDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Offer;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OfferValidator {
    public void defaultOfferServiceValidation(OfferDto offerDto, Long userId) {
        if (!offerDto.getCreatedBy().equals(userId)) {
            throw new DataValidationException("You cant change not yours offer");
        }
    }

    public void createOfferServiceValidation(Offer offer, Long userId) {
        Set<Long> teamMembers = offer.getTeam().getTeamMembers().stream()
                .map(TeamMember::getId).collect(Collectors.toSet());
        if (!teamMembers.contains(userId)) {
            throw new DataValidationException("You are not a part of the team");
        }
    }
}
