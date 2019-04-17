package uk.gov.cslearning.record.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
        learnerService.deleteLearnerByUid(uid);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/foo/persona")
    public ResponseEntity foo() {
        learnerService.deleteOldStatements();

        return ResponseEntity.noContent().build();
    }
}
