package io.permasoft.katas.javaplays.exceptions.persistence.jpa;

import io.permasoft.katas.javaplays.exceptions.api.ExceptionEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
public class ExceptionDaoImpl implements ExceptionDao {

    @Autowired
    public ExceptionDaoImpl() {
    }
}
