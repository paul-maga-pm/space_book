package socialnetwork.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.models.Entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class RepositoryAbstractTest<ID, E extends Entity<ID>> {

    public abstract E getValidEntityThatIsNotInRepository();
    public abstract E getOldValueOfEntityForUpdateTest();
    public abstract E getNewValueForEntityForUpdateTest();
    public abstract ID getNotExistingId();
    public abstract ID getExistingId();
    public abstract E getEntityWithId(ID id);
    public abstract RepositoryInterface<ID, E> getRepository();
    public abstract List<E>  getTestData();

    @Test
    void findReturnsEmptyOptional(){
        var entityOptional = getRepository().findById(getNotExistingId());
        Assertions.assertTrue(entityOptional.isEmpty());
    }

    @Test
    void findReturnsAnEntity(){
        var id = getExistingId();
        var foundEntityOptional = getRepository().findById(id);
        Assertions.assertTrue(foundEntityOptional.isPresent());
        var actualFoundEntity = foundEntityOptional.get();
        var expectedFoundEntity = getEntityWithId(id);
        Assertions.assertEquals(expectedFoundEntity, actualFoundEntity);
    }

    @Test
    void saveShouldAddTheEntity(){
        E validEntity = getValidEntityThatIsNotInRepository();
        Optional<E> existingEntityOptional = getRepository().save(validEntity);
        Assertions.assertTrue(existingEntityOptional.isEmpty());
    }

    @Test
    void entityWithSameIdAlreadyExistsWhenSaving(){
        E validEntity = getValidEntityThatIsNotInRepository();
        Assertions.assertTrue(getRepository().save(validEntity).isEmpty());
        Optional<E> existingEntity = getRepository().save(validEntity);
        Assertions.assertTrue(existingEntity.isPresent());
        Assertions.assertEquals(validEntity, existingEntity.get());
    }

    @Test
    void testGetAll(){
        List<E> expectedEntities = getTestData();
        List<E> actualEntities = getRepository().getAll();
        Assertions.assertTrue(actualEntities.containsAll(expectedEntities));
        Assertions.assertTrue(expectedEntities.containsAll(actualEntities));
    }

    @Test
    void updateReturnsEmptyOptional(){
        var newEntity = getValidEntityThatIsNotInRepository();
        var entityOptional = getRepository().update(newEntity);
        Assertions.assertTrue(entityOptional.isEmpty());
    }

    @Test
    void updateShouldReturnOldValue(){
        var newEntity = getNewValueForEntityForUpdateTest();
        var oldValue = getOldValueOfEntityForUpdateTest();
        var entityOptional = getRepository().update(newEntity);
        Assertions.assertTrue(entityOptional.isPresent());
        Assertions.assertEquals(oldValue, entityOptional.get());
        var updatedEntityFromRepository = getRepository().findById(newEntity.getId());
        Assertions.assertTrue(updatedEntityFromRepository.isPresent());
        Assertions.assertEquals(newEntity, updatedEntityFromRepository.get());
    }


    @Test
    void removeShouldDeleteExistingEntity(){
        var removedEntityOptional = getRepository().remove(getExistingId());
        Assertions.assertTrue(removedEntityOptional.isPresent());
        var actualRemovedEntity = removedEntityOptional.get();
        var expectedRemovedEntity = getEntityWithId(getExistingId());
        Assertions.assertEquals(expectedRemovedEntity, actualRemovedEntity);
    }

    @Test
    void removeShouldNotDeleteAnyEntity(){
        var removedEntityOptional = getRepository().remove(getNotExistingId());
        Assertions.assertTrue(removedEntityOptional.isEmpty());
    }

}
