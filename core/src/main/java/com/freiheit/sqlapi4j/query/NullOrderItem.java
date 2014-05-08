/*
 * (c) Copyright 2014 freiheit.com technologies GmbH
 *
 * Created on 08.05.14 by Ulrich Geilmann (ulrich.geilmann@freiheit.com)
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies GmbH. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies GmbH.
 */
package com.freiheit.sqlapi4j.query;

/**
 * A sort order to which NULLS FIRST/LAST may be applied
 *
 * @author Ulrich Geilmann (ulrich.geilmann@freiheit.com)
 */
public interface NullOrderItem extends OrderItem {

    public enum NullOrder {
        NULLS_FIRST,
        NULLS_LAST
    }

    OrderItem nullsFirst();

    OrderItem nullsLast();

}
