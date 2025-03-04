package uk.gov.cslearning.record.util.stub;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class StubService {

    private final LearningCatalogueStubService learningCatalogueStubService;
    private final CSRSStubService csrsStubService;
    private final IdentityServiceStubService identityServiceStubService;
    private final NotificationServiceStubService notificationServiceStubService;

}
