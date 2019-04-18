package uk.gov.cslearning.record.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.PersonaIdentifiersRepository;
import uk.gov.cslearning.record.repository.PersonasRepository;
import uk.gov.cslearning.record.repository.StatementsRepository;
import uk.gov.cslearning.record.repository.StatesRepository;

@Service
public class CollectionsService {

    private final StatementsRepository statementsRepository;

    private final PersonaIdentifiersRepository personaIdentifiersRepository;

    private final PersonasRepository personasRepository;

    private final StatesRepository statesRepository;

    public CollectionsService(StatementsRepository statementsRepository, PersonaIdentifiersRepository personaIdentifiersRepository, PersonasRepository personasRepository, StatesRepository statesRepository) {
        this.statementsRepository = statementsRepository;
        this.personaIdentifiersRepository = personaIdentifiersRepository;
        this.personasRepository = personasRepository;
        this.statesRepository = statesRepository;
    }

    public void deleteAllByLearnerUid(String uid) {
        statesRepository.deleteAllByLearnerUid(uid);
        statementsRepository.deleteAllByLearnerUid(uid);
        personaIdentifiersRepository.findByLearnerUid(uid).ifPresent(personaidentifiers -> {
            personasRepository.findByUid(personaidentifiers.getId()).ifPresent(personas -> {
                personasRepository.deleteById(personas.getId());
            });
            personaIdentifiersRepository.deleteByLearnerUid(uid);
        });
    }

    public void deleteAllByAge(DateTime dateTime) {
        statesRepository.deleteAllByAge(dateTime);
        statementsRepository.deleteAllByAge(dateTime);
        personaIdentifiersRepository.deleteAllByAge(dateTime);
        personasRepository.deleteAllByAge(dateTime);
    }
}
