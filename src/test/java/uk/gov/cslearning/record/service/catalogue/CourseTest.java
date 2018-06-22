package uk.gov.cslearning.record.service.catalogue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.domain.ModuleRecord;
import uk.gov.cslearning.record.domain.State;
import uk.gov.cslearning.record.repository.CourseRecordRepository;
import uk.gov.cslearning.record.service.CivilServant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CourseTest {

    @Autowired
    private CourseRecordRepository courseRecordRepository;

    final String courseId ="courseId";
    final String userId = "userId";
    final String moduleId = "moduleId";
    final String department = "dep";


    @Test
    public void shouldShowCourseAsCompletedIfAllModulesComplete() {

        List<String> departments = new ArrayList<String>();
        departments.add(department);

        Audience audience = new Audience();
        audience.setDepartments(departments);
        audience.setMandatory(true);

        Collection<Audience> audiences =new HashSet<Audience>();
        audiences.add(audience);

        Module module = new Module();
        module.setId(moduleId);
        module.setAudiences(audiences);

        Collection<Module> modules = new HashSet<>();
        modules.add(module);

        Course course =new Course();
        course.setId(courseId);
        course.setModules(modules);

        CivilServant civilServant = new CivilServant();
        civilServant.setDepartmentCode(department);

        CourseRecord courseRecord = new CourseRecord(courseId, userId);
        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.COMPLETED);
        courseRecord.addModuleRecord(moduleRecord);

        courseRecord = courseRecordRepository.save(courseRecord);
        Collection<CourseRecord> courseRecords = new HashSet<>();
        courseRecords.add(courseRecord);

        assertThat(course.isComplete(courseRecords,civilServant), is(true));
    }

    @Test
    public void shouldNotShowCourseAsCompletedIfNotAllModulesComplete() {

        List<String> departments = new ArrayList<String>();
        departments.add(department);

        Audience audience = new Audience();
        audience.setDepartments(departments);
        audience.setMandatory(true);

        Collection<Audience> audiences =new HashSet<Audience>();
        audiences.add(audience);

        Module module = new Module();
        module.setId(moduleId);
        module.setAudiences(audiences);

        Collection<Module> modules = new HashSet<>();
        modules.add(module);

        Course course =new Course();
        course.setId(courseId);
        course.setModules(modules);

        CivilServant civilServant = new CivilServant();
        civilServant.setDepartmentCode(department);

        CourseRecord courseRecord = new CourseRecord(courseId, userId);
        ModuleRecord moduleRecord = new ModuleRecord(moduleId);
        moduleRecord.setState(State.IN_PROGRESS);
        courseRecord.addModuleRecord(moduleRecord);

        courseRecord = courseRecordRepository.save(courseRecord);
        Collection<CourseRecord> courseRecords = new HashSet<>();
        courseRecords.add(courseRecord);

        assertThat(course.isComplete(courseRecords,civilServant), is(false));
    }

}