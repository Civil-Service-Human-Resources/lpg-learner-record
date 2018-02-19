package uk.gov.cslearning.record.api;

import uk.gov.cslearning.record.domain.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableList;

public class Records {

    private List<Record> records;

    public Records(List<Record> records) {
        checkArgument(records != null, "records is null");
        this.records = new ArrayList<>(records);
    }

    public List<Record> getRecords() {
        return unmodifiableList(records);
    }
}
