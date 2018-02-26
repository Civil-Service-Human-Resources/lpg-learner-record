package uk.gov.cslearning.record.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.record.domain.Registration;
import uk.gov.cslearning.record.service.RegistrationService;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/registrations")
public class LearnerRegistrations {

    private RegistrationService service;

    @Autowired
    public LearnerRegistrations(RegistrationService service) {
        checkArgument(service != null);
        this.service = service;
    }

    @RequestMapping(method = GET)
    public ResponseEntity<Registrations> listAll() {
        List<Registration> registrations = service.getRegistrations();
        return new ResponseEntity<>(new Registrations(registrations), OK);
    }

    public static final class Registrations {

        private List<Registration> registrations;

        public Registrations(List<Registration> registrations) {
            checkArgument(registrations != null, "registrations is null");
            this.registrations = new ArrayList<>(registrations);
        }

        public List<Registration> getRegistrations() {
            return registrations;
        }
    }
}
