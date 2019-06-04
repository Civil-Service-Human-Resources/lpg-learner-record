package uk.gov.cslearning.record.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        LOGGER.info("Deleting learner with uid {}", uid);

        learnerService.deleteLearnerByUid(uid);

        LOGGER.info("Learner deleted with uid {}", uid);

        return ResponseEntity.noContent().build();
    }
}
