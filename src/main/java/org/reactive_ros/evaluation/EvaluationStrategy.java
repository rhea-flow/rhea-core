package org.reactive_ros.evaluation;

import org.reactive_ros.Stream;
import org.reactive_ros.internal.expressions.Transformer;
import org.reactive_ros.internal.graph.FlowGraph;
import org.reactive_ros.internal.output.Output;

import java.io.Serializable;

/**
 * Evaluates a dataflow graph as {@link FlowGraph} containing any topology of {@link Transformer}.
 * @author Orestis Melkonian
 */
public interface EvaluationStrategy extends Serializable {
    <T> void evaluate(Stream<T> stream, Output output);
}
