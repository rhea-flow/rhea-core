package org.rhea_core.optimization.optimizers;

import org.rhea_core.Stream;
import org.rhea_core.internal.expressions.Transformer;
import org.rhea_core.internal.expressions.combining.ConcatMultiExpr;
import org.rhea_core.internal.expressions.combining.MergeMultiExpr;
import org.rhea_core.internal.expressions.filtering.DistinctExpr;
import org.rhea_core.internal.expressions.filtering.FilterExpr;
import org.rhea_core.internal.expressions.filtering.SkipExpr;
import org.rhea_core.internal.expressions.filtering.TakeExpr;
import org.rhea_core.internal.expressions.transformational.MapExpr;
import org.rhea_core.internal.graph.FlowGraph;
import org.rhea_core.internal.graph.SimpleEdge;

/**
 * @author Orestis Melkonian
 */
public class ProactiveFiltering implements Optimizer {

    @Override
    public void optimize(FlowGraph graph) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (SimpleEdge edge : graph.edges()) {
                if (!graph.containsEdge(edge)) continue;
                Transformer source = edge.getSource();
                Transformer target = edge.getTarget();

                // map -> take|skip|distinct
                if (((source instanceof MapExpr) && (target instanceof SkipExpr))
                        || ((source instanceof MapExpr) && (target instanceof DistinctExpr))
                        || ((source instanceof MapExpr) && (target instanceof TakeExpr))) {
                    if (Stream.DEBUG) System.out.println("map -> take|skip|distinct");
                    graph.reorder(source, target);
                    changed = true;
                }
                // map -> filter
                else if ((source instanceof MapExpr) && (target instanceof FilterExpr)) {
                    if (Stream.DEBUG) System.out.println("map -> filter");
                    MapExpr map = (MapExpr) source;
                    FilterExpr filter = (FilterExpr) target;
                    graph.reorder(source, target, map::clone,
                        () -> new FilterExpr<>(i -> ((Boolean) filter.getPredicate().call(map.getMapper().call(i))))
                    );
                    changed = true;
                }
                // concat|merge -> distinct|filter
                else if (((source instanceof ConcatMultiExpr) || (source instanceof MergeMultiExpr))
                        && ((target instanceof DistinctExpr) || (target instanceof FilterExpr))) {
                    if (Stream.DEBUG) System.out.println("concat|merge -> distinct|filter");
                    for (Transformer pred : graph.predecessors(source)) {
                        graph.removeEdge(pred, source);
                        Transformer distinctClone = target.clone();
                        graph.addVertex(distinctClone);
                        graph.addEdge(pred, distinctClone);
                        graph.addEdge(distinctClone, source);
                    }
                    for (Transformer succ : graph.successors(target))
                        graph.addEdge(source, succ);

                    if (graph.getConnectNode().equals(target))
                        graph.setConnectNode(source);

                    graph.removeVertex(target);
                    changed = true;
                }
            }
        }
    }
}
