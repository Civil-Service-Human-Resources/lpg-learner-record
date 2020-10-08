package uk.gov.cslearning.record.notifications;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.domain.Grade;
import uk.gov.cslearning.record.csrs.domain.Interest;
import uk.gov.cslearning.record.csrs.domain.Profession;
import uk.gov.cslearning.record.service.catalogue.Audience;
import uk.gov.cslearning.record.service.scheduler.RequiredCourseUtil;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class RequiredCourseUtilTest {
    private static final String TEST_GRADE_NAME = "Test grade";
    private static final String TEST_GRADE_CODE = "test-grade";
    private static final String INVALID_TEST_GRADE_CODE = "invalid-test-grade";
    private static final String TEST_AREA_OF_WORK_1 = "test-profession-1";
    private static final String TEST_AREA_OF_WORK_2 = "test-profession-2";
    private static final String TEST_AREA_OF_WORK_3 = "test-profession-3";
    private static final String TEST_INTEREST_1 = "test-interest-1";
    private static final String TEST_INTEREST_2 = "test-interest-2";
    private static final String TEST_INTEREST_3 = "test-interest-3";

    @Test
    public void shouldReturnTrueWhenGradeMatches() {
        Audience audience = new Audience();
        audience.setGrades(ImmutableList.of(TEST_GRADE_CODE));
        CivilServant civilServant = new CivilServant();
        Grade grade = new Grade();
        grade.setCode(TEST_GRADE_CODE);
        grade.setName(TEST_GRADE_NAME);
        civilServant.setGrade(grade);

        assertTrue(RequiredCourseUtil.civilServantHasMatchingGrade(audience, civilServant));
    }

    @Test
    public void shouldReturnFalseWhenGradeDoesNotMatch() {
        Audience audience = new Audience();
        audience.setGrades(ImmutableList.of(INVALID_TEST_GRADE_CODE));
        CivilServant civilServant = new CivilServant();
        Grade grade = new Grade();
        grade.setCode(TEST_GRADE_CODE);
        grade.setName(TEST_GRADE_NAME);
        civilServant.setGrade(grade);

        assertFalse(RequiredCourseUtil.civilServantHasMatchingGrade(audience, civilServant));
    }

    @Test
    public void shouldReturnTrueWhenProfessionMatches() {
        Audience audience = new Audience();
        audience.setAreasOfWork(ImmutableList.of(TEST_AREA_OF_WORK_1, TEST_AREA_OF_WORK_2));
        CivilServant civilServant = new CivilServant();
        Profession profession = new Profession();
        profession.setName(TEST_AREA_OF_WORK_1);
        civilServant.setProfession(profession);

        assertTrue(RequiredCourseUtil.civilServantHasMatchingProfession(audience, civilServant));
    }

    @Test
    public void shouldReturnFalseWhenProfessionDoesNotMatch() {
        Audience audience = new Audience();
        audience.setAreasOfWork(ImmutableList.of(TEST_AREA_OF_WORK_1, TEST_AREA_OF_WORK_2));
        CivilServant civilServant = new CivilServant();
        Profession profession = new Profession();
        profession.setName(TEST_AREA_OF_WORK_3);
        civilServant.setProfession(profession);

        assertFalse(RequiredCourseUtil.civilServantHasMatchingProfession(audience, civilServant));
    }

    @Test
    public void shouldReturnTrueWhenOtherAreasOfWorkMatch() {
        Audience audience = new Audience();
        audience.setAreasOfWork(ImmutableList.of(TEST_AREA_OF_WORK_1, TEST_AREA_OF_WORK_2));
        CivilServant civilServant = new CivilServant();
        Profession profession = new Profession();
        profession.setName(TEST_AREA_OF_WORK_1);
        civilServant.setOtherAreasOfWork(ImmutableList.of(profession));

        assertTrue(RequiredCourseUtil.civilServantHasMatchingOtherAreasOfWork(audience, civilServant));
    }

    @Test
    public void shouldReturnTrueWhenOtherAreasOfWorkDoNotMatch() {
        Audience audience = new Audience();
        audience.setAreasOfWork(ImmutableList.of(TEST_AREA_OF_WORK_3));
        CivilServant civilServant = new CivilServant();
        Profession profession1 = new Profession();
        profession1.setName(TEST_AREA_OF_WORK_1);
        Profession profession2 = new Profession();
        profession2.setName(TEST_AREA_OF_WORK_2);
        civilServant.setOtherAreasOfWork(ImmutableList.of(profession1, profession2));

        assertFalse(RequiredCourseUtil.civilServantHasMatchingOtherAreasOfWork(audience, civilServant));
    }

    @Test
    public void shouldReturnTrueWhenInterestsMatch() {
        Audience audience = new Audience();
        audience.setInterests(ImmutableList.of(TEST_INTEREST_1, TEST_INTEREST_2));
        CivilServant civilServant = new CivilServant();
        Interest interest = new Interest();
        interest.setName(TEST_INTEREST_2);
        civilServant.setInterests(ImmutableList.of(interest));

        assertTrue(RequiredCourseUtil.civilServantHasMatchingInterests(audience, civilServant));
    }

    @Test
    public void shouldReturnFalseWhenInterestsDoNotMatch() {
        Audience audience = new Audience();
        audience.setInterests(ImmutableList.of(TEST_INTEREST_1));
        CivilServant civilServant = new CivilServant();
        Interest interest1 = new Interest();
        interest1.setName(TEST_INTEREST_2);
        Interest interest2 = new Interest();
        interest2.setName(TEST_INTEREST_3);
        civilServant.setInterests(ImmutableList.of(interest1, interest2));

        assertFalse(RequiredCourseUtil.civilServantHasMatchingInterests(audience, civilServant));
    }
}
