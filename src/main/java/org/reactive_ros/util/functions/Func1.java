package org.reactive_ros.util.functions;

/**
 * Function with 1 argument.
 * @author Orestis Melkonian
 */
public interface Func1<T, R> extends Function {
    R call(T t);
}
