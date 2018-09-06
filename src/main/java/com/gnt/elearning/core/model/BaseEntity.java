package com.gnt.elearning.core.model;

import io.ebean.Model;
import io.ebean.annotation.ChangeLog;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.SoftDelete;
import io.ebean.annotation.UpdatedTimestamp;
import io.ebean.annotation.WhoCreated;
import io.ebean.annotation.WhoModified;
import java.time.LocalDateTime;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@ChangeLog
public class BaseEntity extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @CreatedTimestamp
    private LocalDateTime createdAt;
    @UpdatedTimestamp
    private LocalDateTime updatedAt;
    @WhoCreated
    private String createdBy;
    @WhoModified
    private String updatedBy;

    @SoftDelete
    private boolean inActive;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }


    public boolean isInActive() {
        return inActive;
    }

    public void setInActive(boolean inActive) {
        this.inActive = inActive;
    }
}
