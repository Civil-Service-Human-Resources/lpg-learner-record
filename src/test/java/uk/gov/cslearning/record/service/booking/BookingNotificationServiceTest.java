package uk.gov.cslearning.record.service.booking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.notifications.dto.MessageDto;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.service.MessageService;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookingNotificationServiceTest {

    private static final String LEARNER_UID = "learner-uid";
    private static final String LINE_MANAGER_EMAIL_ADDRESS = "manager@example.com";
    private static final BookingCancellationReason BOOKING_CANCELLATION_REASON = BookingCancellationReason.ILLNESS;
    private static final String LEARNER_EMAIL_ADDRESS = "learner@example.com";
    @Mock
    private NotificationService notificationService;

    @Mock
    private MessageService messageService;

    @Mock
    private RegistryService registryService;

    @InjectMocks
    private BookingNotificationService bookingNotificationService;

    @Test
    public void shouldSendRegisteredNotifications() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setLearner(LEARNER_UID);

        CivilServant civilServant = new CivilServant();
        civilServant.setLineManagerEmailAddress(LINE_MANAGER_EMAIL_ADDRESS);
        Optional<CivilServant> optionalCivilServant = Optional.of(civilServant);

        MessageDto registeredMessageDto = new MessageDto();
        registeredMessageDto.setRecipient(LEARNER_EMAIL_ADDRESS);
        MessageDto lineManagerRegisteredMessageDto = new MessageDto();
        lineManagerRegisteredMessageDto.setRecipient(LINE_MANAGER_EMAIL_ADDRESS);

        when(registryService.getCivilServantResourceByUid(LEARNER_UID)).thenReturn(optionalCivilServant);
        when(messageService.createRegisteredMessage(bookingDto)).thenReturn(registeredMessageDto);
        when(messageService.createRegisteredMessageForLineManager(bookingDto, civilServant)).thenReturn(lineManagerRegisteredMessageDto);
        when(notificationService.send(registeredMessageDto)).thenReturn(true);
        when(notificationService.send(lineManagerRegisteredMessageDto)).thenReturn(true);

        bookingNotificationService.sendRequestedNotifications(bookingDto);

        verify(notificationService).send(registeredMessageDto);
        verify(notificationService).send(lineManagerRegisteredMessageDto);
    }

    @Test
    public void shouldSendConfirmedNotifications() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setLearner(LEARNER_UID);

        CivilServant civilServant = new CivilServant();
        civilServant.setLineManagerEmailAddress(LINE_MANAGER_EMAIL_ADDRESS);
        Optional<CivilServant> optionalCivilServant = Optional.of(civilServant);

        MessageDto bookedMessageDto = new MessageDto();
        bookedMessageDto.setRecipient(LEARNER_EMAIL_ADDRESS);
        MessageDto lineManagerBookedMessageDto = new MessageDto();
        lineManagerBookedMessageDto.setRecipient(LINE_MANAGER_EMAIL_ADDRESS);

        when(registryService.getCivilServantResourceByUid(LEARNER_UID)).thenReturn(optionalCivilServant);
        when(messageService.createBookedMessage(bookingDto)).thenReturn(bookedMessageDto);
        when(messageService.createBookedMessageForLineManager(bookingDto, civilServant)).thenReturn(lineManagerBookedMessageDto);
        when(notificationService.send(bookedMessageDto)).thenReturn(true);
        when(notificationService.send(lineManagerBookedMessageDto)).thenReturn(true);

        bookingNotificationService.sendConfirmedNotifications(bookingDto);

        verify(notificationService).send(bookedMessageDto);
        verify(notificationService).send(lineManagerBookedMessageDto);
    }

    @Test
    public void shouldSendCancelledNotifications() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setLearner(LEARNER_UID);

        CivilServant civilServant = new CivilServant();
        civilServant.setLineManagerEmailAddress(LINE_MANAGER_EMAIL_ADDRESS);
        Optional<CivilServant> optionalCivilServant = Optional.of(civilServant);

        MessageDto cancelledMessageDto = new MessageDto();
        cancelledMessageDto.setRecipient(LEARNER_EMAIL_ADDRESS);
        MessageDto lineManagerCancelledMessageDto = new MessageDto();
        lineManagerCancelledMessageDto.setRecipient(LINE_MANAGER_EMAIL_ADDRESS);

        BookingStatusDto bookingStatusDto = new BookingStatusDto();
        bookingStatusDto.setCancellationReason("ILLNESS");

        when(registryService.getCivilServantResourceByUid(LEARNER_UID)).thenReturn(optionalCivilServant);
        when(messageService.createUnregisterMessage(bookingDto, BOOKING_CANCELLATION_REASON.getValue())).thenReturn(cancelledMessageDto);
        when(messageService.createCancelledMessageForLineManager(bookingDto, civilServant)).thenReturn(lineManagerCancelledMessageDto);
        when(notificationService.send(cancelledMessageDto)).thenReturn(true);
        when(notificationService.send(lineManagerCancelledMessageDto)).thenReturn(true);

        bookingNotificationService.sendCancelledNotifications(bookingDto, bookingStatusDto);

        verify(notificationService).send(cancelledMessageDto);
        verify(notificationService).send(lineManagerCancelledMessageDto);
    }
}
