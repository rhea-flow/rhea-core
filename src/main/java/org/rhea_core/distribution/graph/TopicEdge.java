package org.rhea_core.distribution.graph;

import org.jgrapht.graph.DefaultEdge;
import org.rhea_core.distribution.hazelcast.HazelcastTopic;
import org.rhea_core.internal.expressions.Transformer;
import org.rhea_core.internal.graph.SimpleEdge;

/**
 * @author Orestis Melkonian
 */
public class TopicEdge extends DefaultEdge {
    private Transformer source;
    private Transformer target;
    private HazelcastTopic topic;

    public TopicEdge(Transformer v1, Transformer v2, HazelcastTopic topic) {
        this.source = v1;
        this.target = v2;
        this.topic = topic;
    }
    public TopicEdge(SimpleEdge e, HazelcastTopic topic) {
        this(e.getSource(), e.getTarget(), topic);
    }

    public HazelcastTopic getTopic() {
        return topic;
    }

    @Override
    public Transformer getSource() {
        return source;
    }

    @Override
    public Transformer getTarget() {
        return target;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj != null) && (obj instanceof TopicEdge)
                && ((TopicEdge) obj).getSource().equals(source)
                && ((TopicEdge) obj).getTarget().equals(target);
    }

    @Override
    public String toString() {
        return topic.getName();
    }
}
