package br.edu.ifpb.veritas.UI;

public class NavePageBuilder {

    private NavPage paginator;

    public static NavPage newNavPage(int currentPage, long totalItems, int totalPages, int pageSize) {
        NavePageBuilder builder = new NavePageBuilder();
        builder.start();
        builder.setCurrentPage(currentPage);
        builder.setTotalItems(totalItems);
        builder.setTotalPages(totalPages);
        builder.setPageSize(pageSize);
        return builder.finish();
    }

    private NavePageBuilder() {
        this.start();
    }

    public NavePageBuilder start() {
        this.paginator = new NavPage();
        return this;
    }

    public NavePageBuilder setCurrentPage(int currentPage) {
        this.paginator.setCurrentPage(currentPage);
        return this;
    }

    public NavePageBuilder setTotalItems(long totalItems) {
        this.paginator.setTotalItems(totalItems);
        return this;
    }

    public NavePageBuilder setTotalPages(int totalPages) {
        this.paginator.setTotalPages(totalPages);
        return this;
    }

    public NavePageBuilder setPageSize(int pageSize) {
        this.paginator.setPageSize(pageSize);
        return this;
    }

    public NavPage finish() {
        return this.paginator;
    }

}
