package com.gnt.elearning.core.model;

import java.util.List;

/**
 * @author nghi
 * @since 2/28/18
 */
public class BatchPayLoad {
    private int count;
    private List<? extends BaseEntity> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<? extends BaseEntity> getList() {
        return list;
    }

    public void setList(List<? extends BaseEntity> list) {
        this.list = list;
    }
}
