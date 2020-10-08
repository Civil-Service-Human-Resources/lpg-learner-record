package uk.gov.cslearning.record.service.scheduler;

import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.domain.Interest;
import uk.gov.cslearning.record.csrs.domain.Profession;
import uk.gov.cslearning.record.service.catalogue.Audience;

public class RequiredCourseUtil {
    private RequiredCourseUtil() {
    }

    public static boolean civilServantHasMatchingGrade(Audience audience, CivilServant civilServant) {
        boolean hasMatchingGrade = false;
        for (String grade : audience.getGrades()) {
            if (civilServant.getGrade() != null && civilServant.getGrade().getCode() != null && civilServant.getGrade().getCode().equals(grade)) {
                hasMatchingGrade = true;
                break;
            }
        }

        return hasMatchingGrade;
    }

    public static boolean civilServantHasMatchingProfession(Audience audience, CivilServant civilServant) {
        boolean hasMatchingProfession = false;
        for (String areaOfWork : audience.getAreasOfWork()) {
            if (civilServant.getProfession() != null
                    && civilServant.getProfession().getName() != null
                    && civilServant.getProfession().getName().equals(areaOfWork)) {
                hasMatchingProfession = true;
                break;
            }
        }

        return hasMatchingProfession;
    }

    public static boolean civilServantHasMatchingOtherAreasOfWork(Audience audience, CivilServant civilServant) {
        boolean hasMatchingAreasOfWork = false;
        for (String areaOfWork : audience.getAreasOfWork()) {
            for (Profession profession : civilServant.getOtherAreasOfWork()) {
                if (profession.getName().equals(areaOfWork)) {
                    hasMatchingAreasOfWork = true;
                    break;
                }
            }
        }

        return hasMatchingAreasOfWork;
    }

    public static boolean civilServantHasMatchingInterests(Audience audience, CivilServant civilServant) {
        boolean hasMatchingInterest = false;
        for (String audienceInterest : audience.getInterests()) {
            for (Interest civilServantInterest : civilServant.getInterests()) {
                if (civilServantInterest != null && civilServantInterest.getName() != null && audienceInterest.equals(civilServantInterest.getName())) {
                    hasMatchingInterest = true;
                    break;
                }
            }
        }

        return hasMatchingInterest;
    }
}
