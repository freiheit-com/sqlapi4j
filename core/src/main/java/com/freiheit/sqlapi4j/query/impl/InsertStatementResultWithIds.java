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

import java.util.List;

/**
 * @author Philip Schwartau (philip.schwartau@freiheit.com)
 */
public class InsertStatementResultWithIds<I> extends InsertStatementResultWithoutIds<I> {

    final List<I> _insertedIds;

    public InsertStatementResultWithIds(final int nofRowsInserted, final List<I> insertedIds) {
        super(nofRowsInserted);
        _insertedIds = insertedIds;
    }

    public List<I> getInsertedIds() throws UnsupportedOperationException {
        return _insertedIds;
    }
}
