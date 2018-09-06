package com.gnt.elearning.core.model;


import java.util.List;

/**
 * @author nghi
 * @since 2/25/18
 */
public class Pagination {
    private List<? extends BaseEntity> list;
    private int totalCount;
    private int pageSize;
    private int pageOffset;

    public Pagination() {
    }

    public Pagination(List<BaseEntity> list) {
        this.list = list;
    }

    public Pagination(List<? extends BaseEntity> list, int pageSize, int pageOffset) {
        this.list = list;
        this.pageSize = pageSize;
        this.pageOffset = pageOffset;
    }

    /**
     * Return the total number of pages based on the page size and total row count.
     */
    public int getTotalPageCount() {
        int rowCount = getTotalCount();
        if (rowCount == 0) {
            return 0;
        } else {
            return ((rowCount - 1) / getPageSize()) + 1;
        }
    }

    /**
     * Return the index position of this page
     */
    public int getPageIndex() {
        if (getPageOffset() == 0) {
            return 1;
        }
        return ((getPageOffset() - 1) / getPageSize()) + 2;
    }

    public boolean hasNext() {
        return (getPageOffset() + getPageSize()) < getTotalCount();
    }

    public boolean hasPrev() {
        return getPageOffset() > 0;
    }

    public List<? extends BaseEntity> getList() {
        return list;
    }

    public void setList(List<BaseEntity> list) {
        this.list = list;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageOffset() {
        return pageOffset;
    }

    public void setPageOffset(int pageOffset) {
        this.pageOffset = pageOffset;
    }

}
