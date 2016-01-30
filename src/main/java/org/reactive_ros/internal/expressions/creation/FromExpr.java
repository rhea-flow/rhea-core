package org.reactive_ros.internal.expressions.creation;

import org.reactive_ros.internal.expressions.NoInputExpr;
import org.reactive_ros.internal.expressions.Transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Orestis Melkonian
 */
public class FromExpr<T> extends NoInputExpr<T> implements Transformer<T> {
    private Iterable<? extends T> collection;

    public FromExpr(Iterable<? extends T> collection) {
        this.collection = collection;
    }

    public Iterable<? extends T> getCollection() {
        return collection;
    }

    public void setCollection(List<? extends T> collection) {
        this.collection = collection;
    }

    @Override
    public Transformer<T> clone() {
        List<T> copied = new ArrayList<>();
        collection.iterator().forEachRemaining(copied::add);
        return new FromExpr<>(copied);
    }

    @Override
    public String toString() {
        return "From: " + collection.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FromExpr))
            return false;

        Iterator<? extends T> it1 = collection.iterator();
        Iterator<? extends T> it2 = ((FromExpr<T>) obj).getCollection().iterator();
        while (it1.hasNext())
            if (!it1.next().equals(it2.next()))
                return false;
        return (!it2.hasNext());
    }
}
