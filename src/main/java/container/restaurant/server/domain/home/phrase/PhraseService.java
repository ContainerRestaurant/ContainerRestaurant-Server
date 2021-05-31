package container.restaurant.server.domain.home.phrase;

import container.restaurant.server.utils.RandomPicker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.of;

@Service
public class PhraseService {

    private final PhraseRepository phraseRepository;

    private final RandomPicker<String> picker;

    public PhraseService(PhraseRepository phraseRepository) {
        this.phraseRepository = phraseRepository;
        picker = new RandomPicker<>(List.of("용기가 모여\n내일을 바꿔요"));
    }

    @PostConstruct
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional(readOnly = true)
    public void updatePhrase() {
        of(phraseRepository.findAll().stream()
                .map(Phrase::getPhrase)
                .collect(Collectors.toList()))
                .filter(strings -> strings.size() > 0)
                .ifPresent(picker::update);
    }

    public String getPhrase() {
        return picker.pick();
    }

}
