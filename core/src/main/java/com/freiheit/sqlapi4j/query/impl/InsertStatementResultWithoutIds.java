/*
 * (c) Copyright 2014 freiheit.com technologies GmbH
 *
 * Created on 26.07.14 by Philip Schwartau (philip.schwartau@freiheit.com)
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies GmbH. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies GmbH.
 */
package com.freiheit.sqlapi4j.query.impl;

import com.freiheit.sqlapi4j.query.statements.InsertStatement;
import java.util.List;

/**
 * @author Philip Schwartau (philip.schwartau@freiheit.com)
 */
public class InsertStatementResultWithoutIds<I> implements InsertStatement.Result<I> {

    final int _nofRowsInserted;

    public InsertStatementResultWithoutIds(final int nofRowsInserted) {
        _nofRowsInserted = nofRowsInserted;
    }

    public int getNofRowsInserted() {
        return _nofRowsInserted;
    }

    public List<I> getInsertedIds() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("no inserted ids in insert result.");
    }
}
