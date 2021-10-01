package io.permasoft.katas.javaplays.exceptions.persistence;

import io.permasoft.katas.javaplays.exceptions.externallibrary.YourUseOfMyLibraryIsInvalid;
import io.permasoft.katas.javaplays.exceptions.persistence.jpa.ExceptionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExceptionStore {
    private static final Logger log = LoggerFactory.getLogger(ExceptionStore.class);
    private ExceptionDao expectionDao;

    @Autowired
    public ExceptionStore(ExceptionDao expectionDao) {
        this.expectionDao = expectionDao;
    }

    public Integer dontThrow() throws YourUseOfMyLibraryIsInvalid {
        return expectionDao.apiWhenLibWantsYouToHandleItsExceptions(1);
    }

    public Integer throwError() {
        return expectionDao.apiWhenApplicationCantKeepRunning(1);
    }

    public Integer throwUnchecked() {
        return expectionDao.apiWhenLibShallWork(-1);
    }

    public Integer throwChecked() throws YourUseOfMyLibraryIsInvalid {
        return expectionDao.apiWhenLibWantsYouToHandleItsExceptions(-1);
    }

    public Integer conditionalThrow (int positiveId) throws YourUseOfMyLibraryIsInvalid {
        return expectionDao.apiWhenLibWantsYouToHandleItsExceptions(positiveId);
    }
}
