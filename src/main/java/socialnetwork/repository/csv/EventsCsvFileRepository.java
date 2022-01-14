package socialnetwork.repository.csv;

import socialnetwork.domain.entities.Event;
import socialnetwork.exceptions.CorruptedDataException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventsCsvFileRepository extends AbstractCSVFileRepository<Long, Event> {
    /**
     * Constructor that creates a new repository that will access the file found at the given path
     *
     * @param filePath absolute path to the file
     */
    public EventsCsvFileRepository(String filePath) {
        super(filePath);
    }

    @Override
    public String entityToString(Event event) {
        String line = "";

        line += event.getId() + ",";
        line += event.getName() + ",";
        line += event.getDescription() + ",";
        line += event.getDate().format(DateTimeFormatter.ISO_DATE) + ",";
        line += event.getImageFile();
        return line;
    }

    @Override
    public Event stringToEntity(String rawEventString) {
        String[] attrs = rawEventString.split(",");

        if (attrs.length != 5)
            throw new CorruptedDataException("event csv file is corrupted");

        Long id = Long.parseLong(attrs[0]);
        String name = attrs[1].stripLeading().stripTrailing();
        String description = attrs[2].stripLeading().stripTrailing();
        LocalDate date = LocalDate.parse(attrs[3].stripLeading().stripTrailing());
        String file = attrs[4].stripLeading().stripTrailing();
        return new Event(id, name, description, date, file);
    }
}
