package faang.school.projectservice.controller;

import faang.school.projectservice.model.dto.DonationDto;
import faang.school.projectservice.model.event.FundRaisedEvent;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

/* Заглушка для задачи BJS2-27281
https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/60?assignee=600ac994bb4eb50078abb00f&selectedIssue=BJS2-27281
 */

@RequiredArgsConstructor
@RestController
@Tag(name = "Donation", description = "API for donations")
@RequestMapping("/donation")
public class DonationController {
    private final FundRaisedEventPublisher fundRaisedEventPublisher;

    @PostMapping
    @Operation(summary = "Create a donation", description = "Create a new donation.")
    public DonationDto saveDonation(@Valid @RequestBody DonationDto donationDto) {
        Random random = new Random();
        Long amount = random.nextLong(1000, 10000);
        Long userId = random.nextLong(1, 10);
        Long projectId = random.nextLong(1, 1000);
        FundRaisedEvent fundRaisedEvent = new FundRaisedEvent(projectId, userId, amount, LocalDateTime.now());
        fundRaisedEventPublisher.publish(fundRaisedEvent);
        return donationDto;
    }
}
