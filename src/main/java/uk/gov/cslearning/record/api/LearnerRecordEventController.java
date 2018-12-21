package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.dto.LearnerRecordEvent;
import uk.gov.cslearning.record.service.LearnerRecordEventService;

import java.util.Collection;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/events")
@PreAuthorize("hasAnyAuthority('DOWNLOAD_BOOKING_FEED')")
public class LearnerRecordEventController {
    private final LearnerRecordEventService learnerRecordEventService;

    public LearnerRecordEventController(LearnerRecordEventService learnerRecordEventService) {
        this.learnerRecordEventService = learnerRecordEventService;
    }

    @GetMapping
    public ResponseEntity<Collection<LearnerRecordEvent>> list() {
        return new ResponseEntity<>(learnerRecordEventService.listEvents(), OK);
    }
}
