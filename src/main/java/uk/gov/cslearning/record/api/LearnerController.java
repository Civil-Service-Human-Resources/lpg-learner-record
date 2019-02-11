package uk.gov.cslearning.record.api;

import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.service.LearnerService;
import uk.gov.cslearning.record.service.UserRecordService;

@RestController
@RequestMapping("/learner")
public class LearnerController {

    private final LearnerService learnerService;

    private final UserRecordService userRecordService;

    public LearnerController(LearnerService learnerService, UserRecordService userRecordService){
        this.learnerService = learnerService;
        this.userRecordService = userRecordService;
    }

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasAnyAuthority('IDENTITY_DELETE')")
    public ResponseEntity deleteLearner(@PathVariable String uid) {
        learnerService.deleteLearnerByUid(uid);

        return ResponseEntity.noContent().build();
    }

    //TEST ENDPOINT - REMOVE!
    @DeleteMapping("/test-delete")
    public ResponseEntity testDeleteStatements() {
        DateTime dateTime = DateTime.now().minusYears(3);
        userRecordService.deleteStatementsOlderThan(dateTime);

        return ResponseEntity.noContent().build();
    }
}
