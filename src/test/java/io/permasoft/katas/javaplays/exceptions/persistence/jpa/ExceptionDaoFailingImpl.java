package io.permasoft.katas.javaplays.exceptions.persistence.jpa;

import io.permasoft.katas.javaplays.exceptions.api.ExceptionEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("cycle")
public class ExceptionDaoFailingImpl implements ExceptionDao {
    private ExceptionEndPoint cycle;

    @Autowired
    public ExceptionDaoFailingImpl(ExceptionEndPoint cycle) {
        this.cycle = cycle;
    }
}
