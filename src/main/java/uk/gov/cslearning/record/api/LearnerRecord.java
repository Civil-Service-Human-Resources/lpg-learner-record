package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.domain.Record;
import uk.gov.cslearning.record.service.LearnerRecordService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/records")
public class LearnerRecord {

    private LearnerRecordService service;

    @Autowired
    public LearnerRecord(LearnerRecordService service) {
        checkArgument(service != null);
        this.service = service;
    }

    @RequestMapping(path = "/{userId}", method = GET)
    public ResponseEntity<Records> search(@PathVariable("userId") String userId,
                                          @RequestParam(name = "activityId", required = false) String activityId,
                                          @RequestParam(name = "state", required = false) String state) {

        List<Record> records = service.getLearnerRecord(userId, activityId);

        if (state != null) {
            records = records.stream()
                    .filter(record -> state.equals(record.getState()))
                    .collect(toList());
        }

        return new ResponseEntity<>(new Records(records), OK);
    }

    public static final class Records {

        private List<Record> records;

        public Records(List<Record> records) {
            checkArgument(records != null, "records is null");
            this.records = new ArrayList<>(records);
        }

        public List<Record> getRecords() {
            return unmodifiableList(records);
        }
    }
}
