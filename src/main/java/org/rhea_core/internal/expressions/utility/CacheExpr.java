package org.rhea_core.internal.expressions.utility;

import org.rhea_core.internal.expressions.SingleInputExpr;
import org.rhea_core.internal.expressions.Transformer;

/**
 * @author Orestis Melkonian
 */
public class CacheExpr<T> extends SingleInputExpr implements Transformer<T> {
    @Override
    public Transformer<T> clone() {
        return new CacheExpr<>();
    }
}
