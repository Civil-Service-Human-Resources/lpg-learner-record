package uk.gov.cslearning.record.csrs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import uk.gov.cslearning.record.domain.identity.Identity;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CivilServant {

    private String fullName;
    private Grade grade = new Grade();
    private OrganisationalUnit organisationalUnit = new OrganisationalUnit();
    private Profession profession = new Profession();
    private List<Profession> otherAreasOfWork = new ArrayList<>();
    private List<Interest> interests = new ArrayList<>();
    private String lineManagerEmailAddress;
    private String lineManagerUid;
    private String lineManagerName;
    private Identity identity;
}
