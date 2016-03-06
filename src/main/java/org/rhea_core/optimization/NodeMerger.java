package org.rhea_core.optimization;

import org.rhea_core.internal.expressions.SingleInputExpr;
import org.rhea_core.internal.expressions.Transformer;
import org.rhea_core.internal.expressions.combining.ConcatMultiExpr;
import org.rhea_core.internal.expressions.combining.MergeMultiExpr;
import org.rhea_core.internal.expressions.combining.ZipExpr;
import org.rhea_core.internal.expressions.conditional_boolean.ExistsExpr;
import org.rhea_core.internal.expressions.creation.FromExpr;
import org.rhea_core.internal.expressions.creation.NeverExpr;
import org.rhea_core.internal.expressions.creation.RepeatExpr;
import org.rhea_core.internal.expressions.filtering.FilterExpr;
import org.rhea_core.internal.expressions.filtering.FilterMapExpr;
import org.rhea_core.internal.expressions.transformational.MapExpr;
import org.rhea_core.internal.graph.FlowGraph;
import org.rhea_core.internal.graph.SimpleEdge;
import org.rhea_core.util.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author Orestis Melkonian
 */
@SuppressWarnings("unchecked")
public class NodeMerger implements Optimizer {

    int granularity;

    public NodeMerger(int granularity) {
        this.granularity = granularity;
    }

    @Override
    public void optimize(FlowGraph graph) {
        int mergeNo = graph.size() - granularity;
        for (int i = 0; i < mergeNo; i++)
            if(!merge(graph)) break;
    }

    public <T,R,R1,R2,T1,T2,T3,T4,T5,T6,T7,T8,T9,X1,X2,X3,X4,X5,X6,X7,X8,X9> 
    boolean merge(FlowGraph graph) {
        for (Transformer vertex : graph.vertices()) {
            if (!graph.containsVertex(vertex)) continue;
            // Meaningless nevers
            if (vertex instanceof NeverExpr) {
                if (!graph.singular(vertex)) continue;

                Transformer succ = graph.successor(vertex);

                // never -> singleInput
                if (succ instanceof SingleInputExpr) {
                    graph.merge(vertex, succ, new NeverExpr());
                    return true;
                }
                // never -> zip
                else if (succ instanceof ZipExpr) {
                    List<Transformer> pred = graph.predecessors(succ);
                    if (pred.stream().anyMatch(n -> !graph.singular(n)))
                        continue;

                    for (Transformer s : graph.successors(succ))
                        graph.addEdge(vertex, s);
                    graph.removeVertex(succ);
                    pred.stream().forEach(graph::removeVertex);
                    return true;
                }
                // never -> merge
                else if (succ instanceof MergeMultiExpr) {
                    List<Transformer> pred = graph.predecessors(succ);
                    if (pred.size() == 2) continue;
                    else {
                        graph.removeVertex(vertex);
                        return true;
                    }
                }
                // never -> concat
                else if (succ instanceof ConcatMultiExpr) {
                    List<Transformer> pred = graph.predecessors(succ);
                    if (pred.stream().anyMatch(n -> !graph.singular(n)))
                        continue;
                    for (int i = 0; i < pred.size(); i++) {
                        if (pred.get(i) instanceof NeverExpr && i > 1) {
                            for (int j = i; j < pred.size(); j++)
                                graph.removeVertex(pred.get(j));
                            break;
                        }
                    }
                    return true;
                }
            }

            // map -> zip
            if (!(vertex instanceof ZipExpr))
                continue;

            ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R> zip = (ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R>) vertex;
            int args = zip.getArgumentNo();

            Func1 f1=null,f2=null,f3=null,f4=null,f5=null,f6=null,f7=null,f8=null,f9=null;
            ZipExpr<X1,X2,X3,X4,X5,X6,X7,X8,X9,R> newZip = new ZipExpr<>(zip.type);

            List<Transformer> predecessors = graph.predecessors(zip);
            for (int i = 1; i <= args; i++) {
                Transformer pred = predecessors.get(i);
                Func1 f = null;
                if (pred instanceof MapExpr && graph.singular(pred)) {
                    f = ((MapExpr) pred).getMapper();
                    Transformer pred2 = graph.predecessor(pred);
                    graph.addEdge(pred2, newZip);
                    graph.removeVertex(pred);
                }
                else
                    graph.addEdge(pred, newZip);


                switch (i) {
                    case 1: f1 = f; break;
                    case 2: f2 = f; break;
                    case 3: f3 = f; break;
                    case 4: f4 = f; break;
                    case 5: f5 = f; break;
                    case 6: f6 = f; break;
                    case 7: f7 = f; break;
                    case 8: f8 = f; break;
                    case 9: f9 = f; break;
                }
            }

            final Func1<X1, T1> F1 = f1; final Func1<X2, T2> F2 = f2; final Func1<X3, T3> F3 = f3;
            final Func1<X4, T4> F4 = f4; final Func1<X5, T5> F5 = f5; final Func1<X6, T6> F6 = f6;
            final Func1<X7, T7> F7 = f7; final Func1<X8, T8> F8 = f8; final Func1<X9, T9> F9 = f9;

            switch (args) {
                case 2:
                    newZip.combiner2 = (i1,i2) -> zip.combiner2.call(F1.call(i1),F2.call(i2)); break;
                case 3:
                    newZip.combiner3 = (i1,i2, i3) -> zip.combiner3.call(F1.call(i1),F2.call(i2),F3.call(i3)); break;
                case 4:
                    newZip.combiner4 = (i1,i2,i3,i4) -> zip.combiner4.call(F1.call(i1),F2.call(i2),F3.call(i3),F4.call(i4)); break;
                case 5:
                    newZip.combiner5 = (i1,i2,i3,i4,i5) -> zip.combiner5.call(F1.call(i1),F2.call(i2),F3.call(i3),F4.call(i4),F5.call(i5)); break;
                case 6:
                    newZip.combiner6 = (i1,i2,i3,i4,i5,i6) -> zip.combiner6.call(F1.call(i1),F2.call(i2),F3.call(i3),F4.call(i4),F5.call(i5),F6.call(i6)); break;
                case 7:
                    newZip.combiner7 = (i1,i2,i3,i4,i5,i6,i7) -> zip.combiner7.call(F1.call(i1),F2.call(i2),F3.call(i3),F4.call(i4),F5.call(i5),F6.call(i6),F7.call(i7)); break;
                case 8:
                    newZip.combiner8 = (i1,i2,i3,i4,i5,i6,i7,i8) -> zip.combiner8.call(
                            F1.call(i1),F2.call(i2),F3.call(i3),F4.call(i4),F5.call(i5),F6.call(i6),F7.call(i7),F8.call(i8)); break;
                case 9:
                    newZip.combiner9 = (i1,i2,i3,i4,i5,i6,i7,i8,i9) -> zip.combiner9.call(
                            F1.call(i1),F2.call(i2),F3.call(i3),F4.call(i4),F5.call(i5),F6.call(i6),F7.call(i7),F8.call(i8), F9.call(i9)); break;
            }
            graph.removeVertex(zip);
            return true;
        }

        for (SimpleEdge edge : graph.edges()) {
            if (!graph.containsEdge(edge)) continue;
            Transformer source = edge.getSource();
            Transformer target = edge.getTarget();

            boolean singular = graph.singular(source);
            Transformer merged = null;

            // map -> map
            if (source instanceof MapExpr && target instanceof MapExpr && singular)
                merged = new MapExpr(i -> ((MapExpr) target).getMapper().call(((MapExpr) source).getMapper().call(i)));
            // from -> map
            else if (source instanceof FromExpr && target instanceof MapExpr && singular) {
                Iterable newCollection =
                        StreamSupport
                                .stream(((FromExpr<? extends T>) source).getCollection().spliterator(), false)
                                .map(i -> ((MapExpr<? super T, ? extends R>) target).getMapper().call(i))::iterator;
                merged = new FromExpr(newCollection);
            }
            // from -> repeat
            else if (source instanceof FromExpr && target instanceof RepeatExpr && singular) {
                int count = ((RepeatExpr) target).getCount();
                if (count == -1) continue;
                List collection = new ArrayList<>();
                ((FromExpr) source).getCollection().forEach(collection::add);
                List newCollection = new ArrayList<>();
                for (int i = 0; i < count; i++) newCollection.addAll(collection);
                merged = new FromExpr(newCollection);
            }
            // map -> filter
            else if (source instanceof MapExpr && target instanceof FilterExpr && singular)
                merged = new FilterMapExpr(((MapExpr) source).getMapper(), ((FilterExpr) target).getPredicate());
            // map -> exists
            else if (source instanceof MapExpr && target instanceof ExistsExpr && singular)
                merged = new ExistsExpr(i -> ((Boolean) (((ExistsExpr) target).getPredicate()).call(((MapExpr) source).getMapper().call(i))));
            // filter -> exists
            else if (source instanceof FilterExpr && target instanceof ExistsExpr && singular)
                merged = new ExistsExpr(i -> ((Boolean) (((FilterExpr) source).getPredicate()).call(i))
                                          && ((Boolean) (((ExistsExpr) target).getPredicate()).call(i)));
            // zip -> map
            else if (source instanceof ZipExpr && target instanceof MapExpr && singular) {
                ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9, R1> zip = (ZipExpr) source;
                Func1<R1, R2> fun = ((MapExpr) target).getMapper();
                if (zip.combiner9 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2, i3, i4, i5, i6, i7, i8, i9) -> fun.call(zip.combiner9.call(i1, i2, i3, i4, i5, i6, i7, i8, i9)));
                if (zip.combiner8 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2, i3, i4, i5, i6, i7, i8) -> fun.call(zip.combiner8.call(i1, i2, i3, i4, i5, i6, i7, i8)));
                if (zip.combiner7 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2, i3, i4, i5, i6, i7) -> fun.call(zip.combiner7.call(i1, i2, i3, i4, i5, i6, i7)));
                if (zip.combiner6 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2, i3, i4, i5, i6) -> fun.call(zip.combiner6.call(i1, i2, i3, i4, i5, i6)));
                if (zip.combiner5 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2, i3, i4, i5) -> fun.call(zip.combiner5.call(i1, i2, i3, i4, i5)));
                if (zip.combiner4 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2, i3, i4) -> fun.call(zip.combiner4.call(i1, i2, i3, i4)));
                if (zip.combiner3 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2, i3) -> fun.call(zip.combiner3.call(i1, i2, i3)));
                if (zip.combiner2 != null)
                    merged = new ZipExpr<T1,T2,T3,T4,T5,T6,T7,T8,T9,R2>(zip.type,
                            (i1, i2) -> fun.call(zip.combiner2.call(i1, i2)));
            }
            else
                continue;

            graph.merge(source, target, merged);
            return true;
        }

        return false;
    }
}