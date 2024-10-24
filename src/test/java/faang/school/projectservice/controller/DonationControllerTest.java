package faang.school.projectservice.controller;

import faang.school.projectservice.model.event.FundRaisedEvent;
import faang.school.projectservice.publisher.FundRaisedEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DonationControllerTest {
    @Mock
    private FundRaisedEventPublisher fundRaisedEventPublisher;

    @Captor
    ArgumentCaptor<FundRaisedEvent> fundRaisedEventArgumentCaptor;

    @Test
    void saveDonation() {
        doNothing().when(fundRaisedEventPublisher).publish(fundRaisedEventArgumentCaptor.capture());

        fundRaisedEventPublisher.publish(any(FundRaisedEvent.class));

        verify(fundRaisedEventPublisher, times(1)).publish(fundRaisedEventArgumentCaptor.capture());
    }
}