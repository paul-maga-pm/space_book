package socialnetwork.repository.csv;


import socialnetwork.domain.models.Entity;
import socialnetwork.exceptions.CorruptedDataException;
import socialnetwork.exceptions.IoFileException;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.List;
import java.util.Optional;

/**
 * Generic implementation of repository with a comma separated value file
 * @param <ID> type of identifier of the models held by the repository
 * @param <E> subclass of Entity with identifier of type ID
 */
public abstract class AbstractCSVFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String filePath;

    /**
     * Constructor that creates a new repository that will access the file found at the given path
     * @param filePath absolute path to the file
     * @throws IoFileException if the file can't be opened
     */
    public AbstractCSVFileRepository(String filePath){
        this.filePath = filePath;
        loadDataFromFile();
    }

    /**
     * Parses the given entity into CSV format
     * @return a String in CSV format containing the fields of entity
     */
    public abstract String entityToString(E entity);

    /**
     * Parses the string that respects csv format into an entity object
     * @param rawEntityString CSV string containing the fields of the entity
     * @throws CorruptedDataException if the given string doesn't respect the CSV format
     */
    public abstract E stringToEntity(String rawEntityString);


    @Override
    public Optional<E> save(E entity) {
        loadDataFromFile();
        Optional<E> existingEntityOptional =  super.save(entity);
        if(existingEntityOptional.isEmpty())
            appendEntityToFile(entity);
        return  existingEntityOptional;
    }

    @Override
    public List<E> getAll() {
        loadDataFromFile();
        return super.getAll();
    }

    @Override
    public Optional<E> findById(ID id) {
        loadDataFromFile();
        return super.findById(id);
    }

    @Override
    public Optional<E> update(E newValue) {
        loadDataFromFile();
        Optional<E> existingEntityOptional = super.update(newValue);
        if(existingEntityOptional.isPresent())
            storeDataToFile();
        return existingEntityOptional;
    }

    @Override
    public Optional<E> remove(ID id) {
        loadDataFromFile();
        Optional<E> existingEntityOptional = super.remove(id);
        if(existingEntityOptional.isPresent())
            storeDataToFile();
        return existingEntityOptional;
    }

    /**
     * Loads all local data to file
     * @throws IoFileException if the file can't be opened
     */
    private void loadDataFromFile() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            super.removeAllLocalData();
            String currentLine;
            while((currentLine = bufferedReader.readLine()) != null){
                currentLine = currentLine.stripLeading().stripTrailing();

                if(currentLine.length() > 0){
                    E currentEntity = stringToEntity(currentLine);
                    super.save(currentEntity);
                }
            }
        } catch (IOException e){
            throw new IoFileException("Can't load data from file " + filePath + "\n");
        }
    }

    /**
     * Stores all local data to file
     * @throws IoFileException if the file can't be opened
     */
    private void storeDataToFile(){
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))){
            for(E entity : super.getAll()){
                bufferedWriter.write(entityToString(entity));
                bufferedWriter.newLine();
            }
        } catch (IOException e){
            throw new IoFileException("Can't load data from file " + filePath + "\n");
        }
    }

    /**
     * Appends the given entity at the end of the file
     * @throws IoFileException if the file can't be opened
     */
    private void appendEntityToFile(E entity) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true))) {
            bufferedWriter.write(entityToString(entity));
            bufferedWriter.newLine();
        } catch (IOException e){
            throw new IoFileException("Can't load data from file " + filePath + "\n");
        }
    }
}
