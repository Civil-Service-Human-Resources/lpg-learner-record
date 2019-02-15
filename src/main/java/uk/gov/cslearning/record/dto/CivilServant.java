package uk.gov.cslearning.record.dto;

import lombok.Data;

@Data
public class CivilServant {
    private String departmentCode;
    private String fullName;
    private String gradeCode;
    private String lineManagerEmail;
    private String lineManagerUid;
    private String profession;
}
