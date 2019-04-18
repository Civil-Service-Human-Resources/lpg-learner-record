package uk.gov.cslearning.record.api;

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

    private final LearnerService learnerService;

    public LearnerController(LearnerService learnerService) {
        this.learnerService = learnerService;
    }

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasAnyAuthority('IDENTITY_DELETE')")
    public ResponseEntity deleteLearner(@PathVariable String uid) {
        new Thread(() -> learnerService.deleteLearnerByUid(uid)).start();

        return ResponseEntity.noContent().build();
    }
}
