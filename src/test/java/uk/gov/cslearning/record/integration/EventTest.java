package uk.gov.cslearning.record.integration;

import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.cslearning.record.IntegrationTestBase;
import uk.gov.cslearning.record.TestDataService;
import uk.gov.cslearning.record.domain.Booking;
import uk.gov.cslearning.record.domain.BookingStatus;
import uk.gov.cslearning.record.domain.Event;
import uk.gov.cslearning.record.domain.Learner;
import uk.gov.cslearning.record.repository.BookingRepository;
import uk.gov.cslearning.record.repository.EventRepository;
import uk.gov.cslearning.record.repository.InviteRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventTest extends IntegrationTestBase {

    private final List<Integer> eventIds = new ArrayList<>();
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private TestDataService testDataService;
    private String course = """
            {
                  "id": "courseId",
                  "title": "Course 1",
                  "shortDescription": "Course 1",
                  "description": "Course 1",
                  "modules": [
                          {
                                  "id": "moduleId",
                                  "type": "face-to-face",
                                  "title": "module1",
                                  "description": "module1",
                                  "optional": false,
                                  "moduleType": "face-to-face",
                                  "cost": 10.0,
                                  "events": [
                                    {
                                        "id": "testEventId",
                                        "dateRanges": [
                                            {
                                                "date": "2025-03-10",
                                                "startTime": "10:00",
                                                "endTime": "11:00"
                                            },
                                            {
                                                "date": "2025-04-10",
                                                "startTime": "10:00",
                                                "endTime": "11:00"
                                            }
                                        ],
                                        "venue": {
                                            "location": "London",
                                            "address": "10 London Road",
                                            "capacity": 10,
                                            "minCapacity": 1
                                        }
                                    }
                                  ]
                          }
                  ],
                  "visibility": "PUBLIC",
                  "status": "Published"
              }""";

    @AfterEach
    public void cleanup() {
        eventRepository.deleteAllById(eventIds);
    }

    private Event generateEvent() {
        Event e = eventRepository.save(testDataService.generateLearnerRecordEvent());
        eventIds.add(e.getId());
        return e;
    }

    @Test
    public void testCreateEvent() throws Exception {
        String json = """
                {
                    "uri": "http://localhost:9001/courses/courseId/modules/moduleId/events/eventId",
                    "status": "ACTIVE"
                }
                """;
        mockMvc.perform(post("/event")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("uid").value("eventId"))
                .andExpect(jsonPath("uri").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/eventId"))
                .andExpect(jsonPath("status").value("Active"));
        assertEquals("/courses/courseId/modules/moduleId/events/eventId", eventRepository.findByUid("eventId").get().getPath());
    }

    @Test
    public void testInviteLearnerToEventAndBook() throws Exception {
        Learner learner = testDataService.generateLearner();
        Event event = generateEvent();
        stubService.getIdentityServiceStubService().getIdentityWithEmail(learner.getLearnerEmail(),
                String.format("""
                        {
                            "username": "%s",
                            "uid": "%s"
                        }
                        """, learner.getLearnerEmail(), learner.getUid()));
        stubService.getLearningCatalogueStubService().getCourse("courseId", course);
        stubService.getNotificationServiceStubService().sendEmail("INVITE_LEARNER",
                String.format("""
                        {
                            "recipient": "%1$s",
                            "personalisation": {
                                "learnerName": "%1$s",
                                "courseTitle": "Course 1",
                                "courseDate": "10 Mar 2025",
                                "courseLocation": "London",
                                "inviteLink": "http://localhost:3001/book/courseId/moduleId/choose-date"
                            },
                            "reference": "UUID"
                        }
                        """, learner.getLearnerEmail()));
        String json = String.format("""
                {
                    "learnerEmail": "%s",
                    "event": "http://localhost:9001%s"
                }
                """, learner.getLearnerEmail(), event.getPath());
        mockMvc.perform(post("/event/" + event.getUid() + "/invitee")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("event").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()))
                .andExpect(jsonPath("learnerEmail").value(learner.getLearnerEmail()));
        assertEquals(learner.getLearnerEmail(), inviteRepository.findAllByEventUid(event.getUid()).stream().toList().get(0).getLearnerEmail());

        // accept booking
        JSONObject civilServant = new JSONObject();
        civilServant.put("fullName", "Learner Name");
        civilServant.put("lineManagerEmailAddress", "lineManager@email.com");
        stubService.getCsrsStubService().getCivilServant(learner.getUid(), civilServant.toString());
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_REQUESTED",
                String.format("""
                        {
                            "recipient": "%1$s",
                            "personalisation": {
                                "learnerName": "%1$s",
                                "courseTitle": "Course 1",
                                "courseDate": "10 Mar 2025",
                                "courseLocation": "London",
                                "accessibility": "Braille",
                                "bookingReference": "Rand1"
                            },
                            "reference": "UUID"
                        }
                        """, learner.getLearnerEmail()));
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_REQUEST_LINE_MANAGER",
                String.format("""
                        {
                            "recipient": "lineManager@email.com",
                            "personalisation": {
                                "recipient": "lineManager@email.com",
                                "learnerName": "Learner Name",
                                "learnerEmail": "%s",
                                "courseTitle": "Course 1",
                                "courseDate": "10 Mar 2025",
                                "courseLocation": "London",
                                "cost": "10.0",
                                "bookingReference": "Rand1"
                            },
                            "reference": "UUID"
                        }
                        """, learner.getLearnerEmail()));
        String bookJson = String.format("""
                {
                    "learner": "%s",
                    "learnerEmail": "%s",
                    "learnerName": "Learner",
                    "event": "http://localhost:9001%s",
                    "status": "REQUESTED",
                    "accessibilityOptions": "Braille"
                }
                """, learner.getUid(), learner.getLearnerEmail(), event.getPath());
        mockMvc.perform(post("/event/" + event.getUid() + "/booking/")
                        .with(csrf())
                        .contentType("application/json")
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value(learner.getUid()))
                .andExpect(jsonPath("learnerEmail").value(learner.getLearnerEmail()))
                .andExpect(jsonPath("event").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()))
                .andExpect(jsonPath("status").value("Requested"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("Rand1"))
                .andExpect(jsonPath("accessibilityOptions").value("Braille"))
                .andExpect(jsonPath("eventUid").value(event.getUid()))
                .andExpect(jsonPath("eventPath").value("/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()));
    }

    @Test
    public void testCreateRequestedBooking() throws Exception {
        String learnerId = testDataService.generateLearnerId();
        String learnerEmail = String.format("%s@email.com", learnerId);
        Event event = generateEvent();
        JSONObject civilServant = new JSONObject();
        civilServant.put("fullName", "Learner Name");
        civilServant.put("lineManagerEmailAddress", "lineManager@email.com");
        stubService.getLearningCatalogueStubService().getCourse("courseId", course);
        stubService.getCsrsStubService().getCivilServant(learnerId, civilServant.toString());
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_REQUESTED",
                String.format("""
                        {
                            "recipient": "%s",
                            "personalisation": {
                                "learnerName": "%s",
                                "courseTitle": "Course 1",
                                "courseDate": "10 Mar 2025",
                                "courseLocation": "London",
                                "accessibility": "Braille",
                                "bookingReference": "Rand1"
                            },
                            "reference": "UUID"
                        }
                        """, learnerEmail, learnerEmail));
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_REQUEST_LINE_MANAGER",
                String.format("""
                        {
                            "recipient": "lineManager@email.com",
                            "personalisation": {
                                "recipient": "lineManager@email.com",
                                "learnerName": "Learner Name",
                                "learnerEmail": "%s",
                                "courseTitle": "Course 1",
                                "courseDate": "10 Mar 2025",
                                "courseLocation": "London",
                                "cost": "10.0",
                                "bookingReference": "Rand1"
                            },
                            "reference": "UUID"
                        }
                        """, learnerEmail));
        String json = String.format("""
                {
                    "learner": "%s",
                    "learnerEmail": "%s",
                    "learnerName": "Learner",
                    "event": "http://localhost:9001%s",
                    "status": "REQUESTED",
                    "accessibilityOptions": "Braille"
                }
                """, learnerId, learnerEmail, event.getPath());
        mockMvc.perform(post("/event/" + event.getUid() + "/booking/")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value(learnerId))
                .andExpect(jsonPath("learnerEmail").value(learnerEmail))
                .andExpect(jsonPath("event").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()))
                .andExpect(jsonPath("status").value("Requested"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("Rand1"))
                .andExpect(jsonPath("accessibilityOptions").value("Braille"))
                .andExpect(jsonPath("eventUid").value(event.getUid()))
                .andExpect(jsonPath("eventPath").value("/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()));
    }

    @Test
    public void testCreateConfirmedBooking() throws Exception {
        Event event = generateEvent();
        JSONObject civilServant = new JSONObject();
        civilServant.put("fullName", "Learner Name");
        civilServant.put("lineManagerEmailAddress", "lineManager@email.com");
        stubService.getLearningCatalogueStubService().getCourse("courseId", course);
        stubService.getCsrsStubService().getCivilServant("learnerUid2", civilServant.toString());
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED", """
                {
                    "recipient": "learnerEmail2@email.com",
                    "personalisation": {
                        "learnerName": "learnerEmail2@email.com",
                        "courseTitle": "Course 1",
                        "courseDate": "10 Mar 2025",
                        "courseLocation": "London",
                        "accessibility": "Braille",
                        "bookingReference": "Rand1"
                    },
                    "reference": "UUID"
                }
                """);
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED_LINE_MANAGER", """
                {
                    "recipient": "lineManager@email.com",
                    "personalisation": {
                        "learnerName": "Learner Name",
                        "learnerEmail": "learnerEmail2@email.com",
                        "recipient": "lineManager@email.com",
                        "courseTitle": "Course 1",
                        "courseDate": "10 Mar 2025",
                        "courseLocation": "London",
                        "cost": "10.0",
                        "bookingReference": "Rand1"
                    },
                    "reference": "UUID"
                }
                """);
        String json = String.format("""
                {
                    "learner": "learnerUid2",
                    "learnerEmail": "learnerEmail2@email.com",
                    "learnerName": "Learner",
                    "event": "http://localhost:9001%s",
                    "status": "CONFIRMED",
                    "accessibilityOptions": "Braille"
                }
                """, event.getPath());
        mockMvc.perform(post("/event/" + event.getUid() + "/booking/")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value("learnerUid2"))
                .andExpect(jsonPath("learnerEmail").value("learnerEmail2@email.com"))
                .andExpect(jsonPath("event").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()))
                .andExpect(jsonPath("status").value("Confirmed"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("confirmationTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("Rand1"))
                .andExpect(jsonPath("accessibilityOptions").value("Braille"))
                .andExpect(jsonPath("eventUid").value(event.getUid()))
                .andExpect(jsonPath("eventPath").value("/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()));
    }

    @Test
    public void testConfirmBooking() throws Exception {
        JSONObject civilServant = new JSONObject();
        civilServant.put("fullName", "Learner Name");
        civilServant.put("lineManagerEmailAddress", "lineManager@email.com");
        Learner learner = new Learner("learnerUid3", "learnerEmail3@email.com");
        Event event = generateEvent();
        Booking booking = testDataService.generateBooking(BookingStatus.REQUESTED, learner);
        event.addBooking(booking);
        eventRepository.save(event);
        bookingRepository.save(booking);
        stubService.getLearningCatalogueStubService().getCourse("courseId", course);
        stubService.getCsrsStubService().getCivilServant("learnerUid3", civilServant.toString());
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED", """
                {
                    "recipient": "learnerEmail3@email.com",
                    "personalisation": {
                        "learnerName": "learnerEmail3@email.com",
                        "courseTitle": "Course 1",
                        "courseDate": "10 Mar 2025",
                        "courseLocation": "London",
                        "accessibility": "",
                        "bookingReference": "ABCDE"
                    },
                    "reference": "UUID"
                }
                """);
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_CONFIRMED_LINE_MANAGER", """
                {
                    "recipient": "lineManager@email.com",
                    "personalisation": {
                        "learnerName": "Learner Name",
                        "learnerEmail": "learnerEmail3@email.com",
                        "recipient": "lineManager@email.com",
                        "courseTitle": "Course 1",
                        "courseDate": "10 Mar 2025",
                        "courseLocation": "London",
                        "cost": "10.0",
                        "bookingReference": "ABCDE"
                    },
                    "reference": "UUID"
                }
                """);
        String json = """
                {
                    "status": "CONFIRMED"
                }
                """;
        mockMvc.perform(patch("/event/" + event.getUid() + "/booking/" + booking.getId())
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value("learnerUid3"))
                .andExpect(jsonPath("learnerEmail").value("learnerEmail3@email.com"))
                .andExpect(jsonPath("event").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()))
                .andExpect(jsonPath("status").value("Confirmed"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("confirmationTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("ABCDE"))
                .andExpect(jsonPath("eventUid").value(event.getUid()))
                .andExpect(jsonPath("eventPath").value("/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()));
    }

    @Test
    public void testCancelBooking() throws Exception {
        JSONObject civilServant = new JSONObject();
        civilServant.put("fullName", "Learner Name");
        civilServant.put("lineManagerEmailAddress", "lineManager@email.com");
        Learner learner = new Learner("learnerUid", "learnerEmail@email.com");
        Event event = generateEvent();
        Booking booking = testDataService.generateBooking(BookingStatus.CONFIRMED, learner);
        event.addBooking(booking);
        eventRepository.save(event);
        bookingRepository.save(booking);
        stubService.getLearningCatalogueStubService().getCourse("courseId", course);
        stubService.getCsrsStubService().getCivilServant("learnerUid", civilServant.toString());
        stubService.getNotificationServiceStubService().sendEmail("CANCEL_BOOKING", """
                {
                    "recipient": "learnerEmail@email.com",
                    "personalisation": {
                        "learnerName": "learnerEmail@email.com",
                        "courseTitle": "Course 1",
                        "courseDate": "10 Mar 2025",
                        "courseLocation": "London",
                        "cancellationReason": "Illness",
                        "bookingReference": "ABCDE"
                    },
                    "reference": "UUID"
                }
                """);
        stubService.getNotificationServiceStubService().sendEmail("BOOKING_CANCELLED_LINE_MANAGER", """
                {
                    "recipient": "lineManager@email.com",
                    "personalisation": {
                        "recipient": "lineManager@email.com",
                        "learnerName": "Learner Name",
                        "learnerEmail": "learnerEmail@email.com",
                        "courseTitle": "Course 1",
                        "courseDate": "10 Mar 2025",
                        "courseLocation": "London",
                        "cost": "10.0",
                        "bookingReference": "ABCDE"
                    },
                    "reference": "UUID"
                }
                """);
        String json = """
                {
                    "status": "CANCELLED",
                    "cancellationReason": "ILLNESS"
                }
                """;
        mockMvc.perform(patch("/event/" + event.getUid() + "/booking/" + booking.getId())
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value("learnerUid"))
                .andExpect(jsonPath("learnerEmail").value("learnerEmail@email.com"))
                .andExpect(jsonPath("event").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()))
                .andExpect(jsonPath("status").value("Cancelled"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("cancellationTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("cancellationReason").value("Illness"))
                .andExpect(jsonPath("bookingReference").value("ABCDE"))
                .andExpect(jsonPath("eventUid").value(event.getUid()))
                .andExpect(jsonPath("eventPath").value("/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()));
    }

    @Test
    public void testCancelEvent() throws Exception {
        Event event = generateEvent();
        Learner learner1 = testDataService.generateLearner();
        Learner learner2 = testDataService.generateLearner();
        Learner learner3 = testDataService.generateLearner();
        Booking booking1 = testDataService.generateBooking(BookingStatus.CONFIRMED, learner1);
        Booking booking2 = testDataService.generateBooking(BookingStatus.REQUESTED, learner2);
        booking2.setBookingReference("BCDEF");
        Booking booking3 = testDataService.generateBooking(BookingStatus.CANCELLED, learner3);
        booking3.setBookingReference("CDEFG");
        List<Booking> bookings = List.of(booking1, booking2, booking3);
        event.setBookings(bookings);
        eventRepository.save(event);
        bookingRepository.saveAll(bookings);
        stubService.getLearningCatalogueStubService().getCourse("courseId", course);
        stubService.getNotificationServiceStubService().sendEmail("CANCEL_EVENT",
                String.format("""
                        {
                            "recipient": "%1$s",
                            "personalisation": {
                                "learnerName": "%1$s",
                                "cancellationReason": "the event is no longer available",
                                "courseTitle": "Course 1",
                                "courseDate": "10 Mar 2025",
                                "courseLocation": "London",
                                "bookingReference": "ABCDE"
                            },
                            "reference": "UUID"
                        }
                        """, learner1.getLearnerEmail()));
        stubService.getNotificationServiceStubService().sendEmail("CANCEL_EVENT",
                String.format("""
                        {
                            "recipient": "%1$s",
                            "personalisation": {
                                "learnerName": "%1$s",
                                "cancellationReason": "the event is no longer available",
                                "courseTitle": "Course 1",
                                "courseDate": "10 Mar 2025",
                                "courseLocation": "London",
                                "bookingReference": "BCDEF"
                            },
                            "reference": "UUID"
                        }
                        """, learner2.getLearnerEmail()));
        String json = """
                {
                    "status": "CANCELLED",
                    "cancellationReason": "UNAVAILABLE"
                }
                """;
        mockMvc.perform(patch("/event/" + event.getUid())
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("uid").value("testEventId"))
                .andExpect(jsonPath("uri").value("http://localhost:9000/learning_catalogue/courses/courseId/modules/moduleId/events/" + event.getUid()))
                .andExpect(jsonPath("status").value("Cancelled"))
                .andExpect(jsonPath("cancellationReason").value("the event is no longer available"));
        stubService.getNotificationServiceStubService().validateSentEmails("CANCEL_EVENT", 2);
    }

}
