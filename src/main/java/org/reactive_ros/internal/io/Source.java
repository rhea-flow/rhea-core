package org.reactive_ros.internal.io;

import org.reactivestreams.Publisher;

/**
 * All inputs should implement this interface.
 * @author Orestis Melkonian
 */
public interface Source<T> extends Publisher<T> {
}
