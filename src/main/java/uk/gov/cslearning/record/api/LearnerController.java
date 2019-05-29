package uk.gov.cslearning.record.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.record.service.LearnerService;

@RestController
@RequestMapping("/learner")
public class LearnerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LearnerController.class);

    private final LearnerService learnerService;

    public LearnerController(LearnerService learnerService) {
        this.learnerService = learnerService;
    }

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasAnyAuthority('IDENTITY_DELETE')")
    public ResponseEntity deleteLearner(@PathVariable String uid) {
        learnerService.deleteLearnerByUid(uid);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/track")
    public ResponseEntity track() {
        LOGGER.info("Deleting old statements");
        learnerService.deleteOldStatements();

        return ResponseEntity.noContent().build();
    }
}
