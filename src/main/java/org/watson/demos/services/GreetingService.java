package org.watson.demos.services;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.watson.demos.models.Greeting;
import org.watson.demos.models.GreetingProbe;
import org.watson.demos.repositories.GreetingRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GreetingService {

    private final GreetingRepository repository;

    public Optional<Greeting> getOne(final UUID id) {
        return repository.findById(id);
    }

    public Iterable<Greeting> getAll(@NonNull final GreetingProbe probe) {
        if (probe.getLocale() != null) {
            return repository.findAllByLocale(probe.getLocale());
        } else {
            return repository.findAll();
        }
    }

    public Greeting create(@NonNull final Greeting greeting) {
        return repository.save(greeting);
    }

    public void delete(@NonNull final UUID id) {
        repository.deleteById(id);
    }
}
