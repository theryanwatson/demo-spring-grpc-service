package org.watson.demos.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.watson.demos.models.Greeting;

import java.util.Locale;
import java.util.UUID;

@Repository
public interface GreetingRepository extends CrudRepository<Greeting, UUID> {
    Iterable<Greeting> findAllByLocale(Locale locale);
}
