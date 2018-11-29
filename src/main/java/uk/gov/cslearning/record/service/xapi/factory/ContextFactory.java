package uk.gov.cslearning.record.service.xapi.factory;

import gov.adlnet.xapi.model.Activity;
import gov.adlnet.xapi.model.Context;
import gov.adlnet.xapi.model.ContextActivities;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.record.dto.BookingDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.gov.cslearning.record.service.xapi.activity.Activity.COURSE_ID_PREFIX;
import static uk.gov.cslearning.record.service.xapi.activity.Activity.MODULE_ID_PREFIX;

@Component
public class ContextFactory {
    public Context createBookingContext(BookingDto bookingDto) {
        Pattern pattern = Pattern.compile("\\/courses\\/([^\\/]+)\\/modules\\/([^\\/]+)\\/.*");
        Matcher matcher = pattern.matcher(bookingDto.getEvent().getPath());

        if (!matcher.matches()) {
            throw new IllegalStateException("Unable to parse event URI");
        }

        String courseId = matcher.group(1);
        String moduleId = matcher.group(2);

        Activity courseActivity = new Activity(String.join("/", COURSE_ID_PREFIX, courseId));
        Activity moduleActivity = new Activity(String.join("/", MODULE_ID_PREFIX, moduleId));

        ContextActivities contextActivities = new ContextActivities();
        contextActivities.setParent(new ArrayList<>(Arrays.asList(courseActivity, moduleActivity)));

        Context context = new Context();
        context.setContextActivities(contextActivities);

        return context;
    }
}
