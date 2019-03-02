package it.fvaleri.integ;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

@ApplicationScoped
public class FruitService {

    @Singleton
    FruitRepository repository;

    public CompletionStage<List<Fruit>> findAll() {
        return repository.findAll();
    }

    public CompletionStage<Fruit> findById(Long id) {
        return repository.findById(id);
    }

    public CompletionStage<Long> save(Fruit fruit) {
        return repository.save(fruit);
    }

    public CompletionStage<Boolean> delete(Long id) {
        return repository.delete(id);
    }

}
