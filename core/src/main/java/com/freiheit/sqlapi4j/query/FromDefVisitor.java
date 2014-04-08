package com.freiheit.sqlapi4j.query;

import com.freiheit.sqlapi4j.meta.TableAlias;
import com.freiheit.sqlapi4j.meta.TableDef;
import com.freiheit.sqlapi4j.query.impl.JoinDecl;

import javax.annotation.Nonnull;

/**
 * A visitor for {@link com.freiheit.sqlapi4j.query.FromDef}s.
 *
 * @author Philip Schwartau (philip.schwartau@freiheit.com)
 */
public interface FromDefVisitor<T> {

    T visit(@Nonnull TableDef tableDef);

    T visit(@Nonnull JoinDecl joinDecl);

    T visit(@Nonnull TableAlias tableAlias);

}
