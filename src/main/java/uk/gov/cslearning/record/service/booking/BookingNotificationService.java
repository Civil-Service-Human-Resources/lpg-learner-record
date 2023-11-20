package uk.gov.cslearning.record.service.booking;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.service.RegistryService;
import uk.gov.cslearning.record.dto.BookingCancellationReason;
import uk.gov.cslearning.record.dto.BookingDto;
import uk.gov.cslearning.record.dto.BookingStatusDto;
import uk.gov.cslearning.record.exception.CivilServantNotFoundException;
import uk.gov.cslearning.record.notifications.service.NotificationService;
import uk.gov.cslearning.record.service.MessageService;

@Service
public class BookingNotificationService {
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final RegistryService registryService;

    public BookingNotificationService(NotificationService notificationService, MessageService messageService, RegistryService registryService) {
        this.notificationService = notificationService;
        this.messageService = messageService;
        this.registryService = registryService;
    }

    public void sendRequestedNotifications(BookingDto bookingDto) {
        String learnerUid = bookingDto.getLearner();

        CivilServant civilServant = registryService.getCivilServantResourceByUid(learnerUid).orElseThrow(() -> new CivilServantNotFoundException(learnerUid));

        notificationService.send(messageService.createRegisteredMessage(bookingDto));
        notificationService.send(messageService.createRegisteredMessageForLineManager(bookingDto, civilServant));
    }

    public void sendConfirmedNotifications(BookingDto bookingDto) {
        String learnerUid = bookingDto.getLearner();

        CivilServant civilServant = registryService.getCivilServantResourceByUid(learnerUid).orElseThrow(() -> new CivilServantNotFoundException(learnerUid));

        notificationService.send(messageService.createBookedMessage(bookingDto));
        notificationService.send(messageService.createBookedMessageForLineManager(bookingDto, civilServant));
    }

    public void sendCancelledNotifications(BookingDto bookingDto, BookingStatusDto bookingStatusDto) {
        String learnerUid = bookingDto.getLearner();

        CivilServant civilServant = registryService.getCivilServantResourceByUid(learnerUid).orElseThrow(() -> new CivilServantNotFoundException(learnerUid));

        bookingDto.setCancellationReason(BookingCancellationReason.valueOf(bookingStatusDto.getCancellationReason()));

        notificationService.send(messageService.createUnregisterMessage(bookingDto, bookingDto.getCancellationReason().getValue()));
        notificationService.send(messageService.createCancelledMessageForLineManager(bookingDto, civilServant));
    }
}
