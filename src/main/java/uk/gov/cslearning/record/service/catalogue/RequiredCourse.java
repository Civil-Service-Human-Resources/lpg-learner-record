package uk.gov.cslearning.record.service.catalogue;

import lombok.Setter;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Setter
public class RequiredCourse extends Course {

    private final Map<String, LearningPeriod> learningPeriodMap;

    public RequiredCourse(String id, String title, Collection<Module> modules, Collection<Audience> audiences, Map<String, LearningPeriod> learningPeriodMap) {
        super(id, title, modules, audiences);
        this.learningPeriodMap = learningPeriodMap;
    }

    public Optional<LearningPeriod> getLearningPeriod(String departmentCode) {
        return Optional.ofNullable(learningPeriodMap.get(departmentCode));
    }

}
