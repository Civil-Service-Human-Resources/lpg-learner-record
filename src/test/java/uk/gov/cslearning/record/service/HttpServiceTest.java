package uk.gov.cslearning.record.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.record.domain.CourseRecord;
import uk.gov.cslearning.record.service.scheduler.LearningJob;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LearningJob.class);

    //    public static final String EXAMPLE_URL = "http://example.com/api/identities";
    public static final String EXAMPLE_URL = "http://identity.local.cshr.digital:8081/api/identities";

    @InjectMocks
    private HttpService httpService;

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private UserRecordService userRecordService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void httpGet() throws Exception{
        Object[] mockedResponse = new Object[2];
        mockedResponse[0] = "Test1";
        mockedResponse[1] = "Test2";

        when(restTemplate.getForObject(EXAMPLE_URL, Object[].class))
                .thenReturn(mockedResponse);

        Object[] response = httpService.httpGet(EXAMPLE_URL);
        assertThat(response, notNullValue());
        assertThat(response[0], equalTo("Test1"));
        assertThat(response[1], equalTo("Test2"));
    }

    @Test
    public void tester(){
        RestTemplate restTemplate = new RestTemplate();
        Object[] response = restTemplate.getForObject(EXAMPLE_URL, Object[].class);
        for (Object o: response){
            LinkedHashMap<String, String> identity = (LinkedHashMap<String, String>) o;
            String uid = identity.get("uid");
            String username = identity.get("username");
            Collection<CourseRecord> courses = userRecordService.getUserRecord(uid, "");
        }
        System.out.print("");

    }
}