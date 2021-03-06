package org.rhea_core.io;

import org.rhea_core.serialization.SerializationStrategy;

/**
 * @author Orestis Melkonian
 */
public abstract class InternalTopic<T, C, B> extends ExternalTopic<T, C> {

    protected SerializationStrategy<B> serializationStrategy;

    protected InternalTopic(String name, SerializationStrategy serializationStrategy) {
        super(name);
        this.serializationStrategy = serializationStrategy;
    }
}
