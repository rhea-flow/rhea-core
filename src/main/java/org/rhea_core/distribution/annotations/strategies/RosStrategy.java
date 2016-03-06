package org.rhea_core.distribution.annotations.strategies;

import org.rhea_core.Stream;
import org.rhea_core.distribution.annotations.StrategyInfo;
import org.rhea_core.evaluation.EvaluationStrategy;
import org.rhea_core.internal.output.Output;

/**
 * @author Orestis Melkonian
 */
@StrategyInfo(requiredSkills = {"Ros"}, priority = 1)
public class RosStrategy implements EvaluationStrategy {
    @Override
    public <T> void evaluate(Stream<T> stream, Output output) {

    }
}