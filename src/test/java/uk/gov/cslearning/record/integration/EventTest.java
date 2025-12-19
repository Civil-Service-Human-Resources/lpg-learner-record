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
                    "uid": "eventId",
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
                .andExpect(jsonPath("status").value("Active"));
    }

    @Test
    public void testInviteLearnerToEventAndBook() throws Exception {
        String learnerUid = "learnerUid";
        String learnerEmail = "learnerEmail@email.com";
        Event event = generateEvent();
        String json = String.format("""
                {
                    "learnerEmail": "%s",
                    "learnerUid": "%s",
                    "eventUid": "%s"
                }
                """, learnerEmail, learnerUid, event.getUid());
        mockMvc.perform(post("/event/" + event.getUid() + "/invitee")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learnerEmail").value(learnerEmail))
                .andExpect(jsonPath("learnerUid").value(learnerUid));
        assertEquals(learnerEmail, inviteRepository.findAllByEventUid(event.getUid()).stream().toList().get(0).getLearnerEmail());

        // accept booking
        String bookJson = String.format("""
                {
                    "learner": "%s",
                    "learnerName": "Learner",
                    "eventUid": "%s",
                    "status": "REQUESTED",
                    "accessibilityOptions": "Braille"
                }
                """, learnerUid, event.getUid());
        mockMvc.perform(post("/event/" + event.getUid() + "/booking/")
                        .with(csrf())
                        .contentType("application/json")
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value(learnerUid))
                .andExpect(jsonPath("status").value("Requested"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("Rand1"))
                .andExpect(jsonPath("accessibilityOptions").value("Braille"))
                .andExpect(jsonPath("eventUid").value(event.getUid()));
    }

    @Test
    public void testCreateRequestedBooking() throws Exception {
        String learnerId = testDataService.generateLearnerId();
        String learnerEmail = String.format("%s@email.com", learnerId);
        Event event = generateEvent();
        String json = String.format("""
                {
                    "learner": "%s",
                    "learnerEmail": "%s",
                    "learnerName": "Learner",
                    "eventUid": "%s",
                    "status": "REQUESTED",
                    "accessibilityOptions": "Braille"
                }
                """, learnerId, learnerEmail, event.getUid());
        mockMvc.perform(post("/event/" + event.getUid() + "/booking/")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value(learnerId))
                .andExpect(jsonPath("status").value("Requested"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("Rand1"))
                .andExpect(jsonPath("accessibilityOptions").value("Braille"))
                .andExpect(jsonPath("eventUid").value(event.getUid()));
    }

    @Test
    public void testCreateConfirmedBooking() throws Exception {
        Event event = generateEvent();
        String json = String.format("""
                {
                    "learner": "learnerUid2",
                    "learnerEmail": "learnerEmail2@email.com",
                    "learnerName": "Learner",
                    "eventUid": "%s",
                    "status": "CONFIRMED",
                    "accessibilityOptions": "Braille"
                }
                """, event.getUid());
        mockMvc.perform(post("/event/" + event.getUid() + "/booking/")
                        .with(csrf())
                        .contentType("application/json")
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(Matchers.any(Integer.class)))
                .andExpect(jsonPath("learner").value("learnerUid2"))
                .andExpect(jsonPath("status").value("Confirmed"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("confirmationTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("Rand1"))
                .andExpect(jsonPath("accessibilityOptions").value("Braille"))
                .andExpect(jsonPath("eventUid").value(event.getUid()));
    }

    @Test
    public void testConfirmBooking() throws Exception {
        String learnerUid = "learnerUid3";
        Event event = generateEvent();
        Booking booking = testDataService.generateBooking(BookingStatus.REQUESTED, learnerUid);
        event.addBooking(booking);
        eventRepository.save(event);
        bookingRepository.save(booking);
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
                .andExpect(jsonPath("status").value("Confirmed"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("confirmationTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("bookingReference").value("ABCDE"))
                .andExpect(jsonPath("eventUid").value(event.getUid()));
    }

    @Test
    public void testCancelBooking() throws Exception {
        JSONObject civilServant = new JSONObject();
        civilServant.put("fullName", "Learner Name");
        civilServant.put("lineManagerEmailAddress", "lineManager@email.com");
        String learnerUid = "learnerUid";
        Event event = generateEvent();
        Booking booking = testDataService.generateBooking(BookingStatus.CONFIRMED, learnerUid);
        event.addBooking(booking);
        eventRepository.save(event);
        bookingRepository.save(booking);
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
                .andExpect(jsonPath("status").value("Cancelled"))
                .andExpect(jsonPath("bookingTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("cancellationTime").value("2023-01-01T10:00:00Z"))
                .andExpect(jsonPath("cancellationReason").value("Illness"))
                .andExpect(jsonPath("bookingReference").value("ABCDE"))
                .andExpect(jsonPath("eventUid").value(event.getUid()));
    }

}
