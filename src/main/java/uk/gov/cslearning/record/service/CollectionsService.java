package uk.gov.cslearning.record.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.record.repository.PersonaIdentifiersRepository;
import uk.gov.cslearning.record.repository.PersonasRepository;
import uk.gov.cslearning.record.repository.StatementsRepository;

@Service
public class CollectionsService {

    private final StatementsRepository statementsRepository;

    private final PersonaIdentifiersRepository personaIdentifiersRepository;

    private final PersonasRepository personasRepository;

    public CollectionsService(StatementsRepository statementsRepository, PersonaIdentifiersRepository personaIdentifiersRepository, PersonasRepository personasRepository) {
        this.statementsRepository = statementsRepository;
        this.personaIdentifiersRepository = personaIdentifiersRepository;
        this.personasRepository = personasRepository;
    }

    public void deleteAllByLearnerUid(String uid) {
        statementsRepository.deleteAllByLearnerUid(uid);
        personaIdentifiersRepository.findByLearnerUid(uid).ifPresent(personaidentifiers -> {
            personasRepository.findByUid(personaidentifiers.getId()).ifPresent(personas -> {
                personasRepository.deleteById(personas.getId());
            });
            personaIdentifiersRepository.deleteByLearnerUid(uid);
        });
    }

    public void deleteAllByAge(DateTime dateTime) {
        statementsRepository.deleteAllByAge(dateTime);
        personaIdentifiersRepository.deleteAllByAge(dateTime);
        personasRepository.deleteAllByAge(dateTime);
    }
}
