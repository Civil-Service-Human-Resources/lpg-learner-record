package uk.gov.cslearning.record.domain.record;

import lombok.Getter;

@Getter
public enum LearnerRecordTypeEnum {
    COURSE(1),
    MODULE(2),
    EVENT(3);

    private final Integer id;

    LearnerRecordTypeEnum(Integer id) {
        this.id = id;
    }
}
