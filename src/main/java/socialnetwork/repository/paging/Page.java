package socialnetwork.repository.paging;
import java.util.stream.Stream;

public class Page<T> implements PageInterface<T> {
    private PageableInterface pageable;
    private Stream<T> content;

    Page(PageableInterface pageable, Stream<T> content) {
        this.pageable = pageable;
        this.content = content;
    }

    @Override
    public PageableInterface getPageable() {
        return this.pageable;
    }

    @Override
    public PageableInterface nextPageable() {
        return new Pageable(this.pageable.getPageNumber() + 1, this.pageable.getPageSize());
    }

    @Override
    public Stream<T> getContent() {
        return this.content;
    }
}
