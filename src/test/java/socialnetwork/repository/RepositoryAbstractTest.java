package socialnetwork.repository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import socialnetwork.domain.models.Entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class RepositoryAbstractTest<ID, E extends Entity<ID>> {

    public abstract E createValidEntityThatIsNotInRepository();
    public abstract ID createNotExistingId();
    public abstract ID getExistingId();
    public abstract RepositoryInterface<ID, E> getRepository();
    public abstract List<E>  getTestData();

    @Test
    void findReturnsEmptyOptional(){
        var entityOptional = getRepository().findById(createNotExistingId());
        Assertions.assertTrue(entityOptional.isEmpty());
    }

    @Test
    void findReturnsAnEntity(){
        var id = getExistingId();
        var foundEntityOptional = getRepository().findById(id);
        Assertions.assertTrue(foundEntityOptional.isPresent());
        var foundEntity = foundEntityOptional.get();
        Predicate<E> areEntitiesEqual = entity -> entity.equals(foundEntity);
        boolean isFoundEntityInTestData = getTestData()
                .stream()
                .anyMatch(areEntitiesEqual);
        Assertions.assertTrue(isFoundEntityInTestData);
    }

    @Test
    void saveShouldAddTheEntity(){
        E validEntity = createValidEntityThatIsNotInRepository();
        Optional<E> existingEntityOptional = getRepository().save(validEntity);
        Assertions.assertTrue(existingEntityOptional.isEmpty());
    }

    @Test
    void entityWithSameIdAlreadyExistsWhenSaving(){
        E validEntity = createValidEntityThatIsNotInRepository();
        Assertions.assertTrue(getRepository().save(validEntity).isEmpty());
        Optional<E> existingEntity = getRepository().save(validEntity);
        Assertions.assertTrue(existingEntity.isPresent());
    }

    @Test
    void testGetAll(){
        List<E> expectedEntities = getTestData();
        List<E> actualEntities = getRepository().getAll();

        Assertions.assertEquals(expectedEntities.size(), actualEntities.size());
        for(E entity : actualEntities)
            Assertions.assertTrue(expectedEntities.contains(entity));
    }

    @Test
    void updateReturnsEmptyOptional(){
        var newEntity = createValidEntityThatIsNotInRepository();
        newEntity.setId(createNotExistingId());
        var entityOptional = getRepository().update(newEntity);
        Assertions.assertTrue(entityOptional.isEmpty());
    }

    @Test
    void updateShouldReturnOldValue(){
        var newEntity = createValidEntityThatIsNotInRepository();
        List<E> testData = getTestData();
        newEntity.setId(testData.get(0).getId());
        var oldValue = testData.get(0);
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
        var removedEntity = removedEntityOptional.get();
        var testData = getTestData();
        Predicate<E> isEntityEqualTo = entity -> entity.equals(removedEntity);
        var isRemovedEntityInTestData = testData
                .stream()
                .anyMatch(isEntityEqualTo);
        Assertions.assertTrue(isRemovedEntityInTestData);
    }

    @Test
    void removeShouldNotDeleteAnyEntity(){
        var removedEntityOptional = getRepository().remove(createNotExistingId());
        Assertions.assertTrue(removedEntityOptional.isEmpty());
    }

}
