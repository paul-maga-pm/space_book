package socialnetwork.repository.paging;


import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Paginator<E> {
    private PageableInterface pageable;
    private Iterable<E> elements;

    public Paginator(PageableInterface pageable, Iterable<E> elements) {
        this.pageable = pageable;
        this.elements = elements;
    }

    public PageInterface<E> paginate() {
        Stream<E> result = StreamSupport.stream(elements.spliterator(), false)
                .skip(pageable.getPageNumber()  * pageable.getPageSize())
                .limit(pageable.getPageSize());
        return new Page<>(pageable, result);
    }
}
