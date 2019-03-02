package it.fvaleri.integ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.vertx.axle.pgclient.PgPool;
import io.vertx.axle.sqlclient.Row;
import io.vertx.axle.sqlclient.RowSet;
import io.vertx.axle.sqlclient.Tuple;

@ApplicationScoped
public class FruitRepository {

    @Inject
    PgPool client;

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    @PostConstruct
    void config() {
        if (schemaCreate) {
            initdb();
        }
    }

    private void initdb() {
        // join because post contruct is invoked synchronously
        client.query("DROP TABLE IF EXISTS fruits")
                .thenCompose(r -> client.query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT UNIQUE NOT NULL)"))
                .thenCompose(r -> client.query("INSERT INTO fruits (name) VALUES ('Orange')"))
                .thenCompose(r -> client.query("INSERT INTO fruits (name) VALUES ('Pear')"))
                .thenCompose(r -> client.query("INSERT INTO fruits (name) VALUES ('Apple')"))
                .toCompletableFuture()
                .join();
    }

    public CompletionStage<List<Fruit>> findAll() {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC").thenApply(pgRowSet -> {
            List<Fruit> list = new ArrayList<>(pgRowSet.size());
            for (Row row : pgRowSet) {
                list.add(from(row));
            }
            return list;
        });
    }

    public CompletionStage<Fruit> findById(Long id) {
        return client.preparedQuery("SELECT id, name FROM fruits WHERE id = $1", Tuple.of(id))
                .thenApply(RowSet::iterator)
                .thenApply(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public CompletionStage<Fruit> findByName(String name) {
        return client.preparedQuery("SELECT id, name FROM fruits WHERE name = $1", Tuple.of(name))
                .thenApply(RowSet::iterator)
                .thenApply(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    private Fruit from(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }

    public CompletionStage<Long> save(Fruit fruit) {
        return client.preparedQuery("INSERT INTO fruits (name) VALUES ($1) RETURNING (id)", Tuple.of(fruit.name))
                .thenApply(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
    }

    public CompletionStage<Boolean> delete(Long id) {
        return client.preparedQuery("DELETE FROM fruits WHERE id = $1", Tuple.of(id))
                .thenApply(pgRowSet -> pgRowSet.rowCount() == 1);
    }

}
