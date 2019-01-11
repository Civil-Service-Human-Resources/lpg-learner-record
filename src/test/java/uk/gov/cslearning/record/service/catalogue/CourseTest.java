package uk.gov.cslearning.record.service.catalogue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.csrs.domain.CivilServant;
import uk.gov.cslearning.record.csrs.domain.OrganisationalUnit;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CourseTest {

    final String courseId = "courseId";
    final String userId = "userId";
    final String moduleId = "moduleId";
    final String department = "dep";
    @Autowired
    private CourseRecordRepository courseRecordRepository;

    @Test
    public void shouldShowCourseAsCompletedIfAllModulesComplete() {
        List<String> departments = new ArrayList<>();
        departments.add(department);

        Audience audience = new Audience();
        audience.setDepartments(departments);
        audience.setMandatory(true);

        Collection<Audience> audiences = new HashSet<Audience>();
        audiences.add(audience);

        Module module = new Module();
        module.setId(moduleId);

        Collection<Module> modules = new HashSet<>();
        modules.add(module);

        Course course = new Course();
        course.setId(courseId);
        course.setModules(modules);
        course.setAudiences(audiences);

        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setCode(department);

        CivilServant civilServant = new CivilServant();
        civilServant.setOrganisationalUnit(organisationalUnit);

        CourseRecord courseRecord = new CourseRecord(courseId, userId);
        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.COMPLETED);
        courseRecord.addModuleRecord(moduleRecord);

        courseRecord = courseRecordRepository.save(courseRecord);
        Collection<CourseRecord> courseRecords = new HashSet<>();
        courseRecords.add(courseRecord);

        assertThat(course.isComplete(courseRecords), is(true));
    }

    @Test
    public void shouldNotShowCourseAsCompletedIfNotAllModulesComplete() {
        List<String> departments = new ArrayList<String>();
        departments.add(department);

        Audience audience = new Audience();
        audience.setDepartments(departments);
        audience.setMandatory(true);

        Collection<Audience> audiences = new HashSet<Audience>();
        audiences.add(audience);

        Module module = new Module();
        module.setId(moduleId);

        Collection<Module> modules = new HashSet<>();
        modules.add(module);

        Course course = new Course();
        course.setId(courseId);
        course.setModules(modules);
        course.setAudiences(audiences);

        OrganisationalUnit organisationalUnit = new OrganisationalUnit();
        organisationalUnit.setCode(department);

        CivilServant civilServant = new CivilServant();
        civilServant.setOrganisationalUnit(organisationalUnit);

        CourseRecord courseRecord = new CourseRecord(courseId, userId);
        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.IN_PROGRESS);
        courseRecord.addModuleRecord(moduleRecord);

        courseRecord = courseRecordRepository.save(courseRecord);
        Collection<CourseRecord> courseRecords = new HashSet<>();
        courseRecords.add(courseRecord);

        assertThat(course.isComplete(courseRecords), is(false));
    }
}