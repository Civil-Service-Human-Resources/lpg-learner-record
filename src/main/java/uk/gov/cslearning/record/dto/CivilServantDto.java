package uk.gov.cslearning.record.dto;

import lombok.Data;

@Data
public class CivilServantDto {
    private String id;
    private String uid;
    private String name;
    private String email;
    private String organisation;
    private String profession;
    private String otherAreasOfWork;
    private String grade;
    private String lineManagerUid;

    public CivilServantDto(Long id, String name, String organisation, String profession, String uid, String grade, String lineManagerUid) {
        this.id = id.toString();
        this.name = name;
        this.organisation = organisation;
        this.profession = profession;
        this.uid = uid;
        this.grade = grade;
        this.lineManagerUid = lineManagerUid;
    }

    public CivilServantDto() {
    }
}
