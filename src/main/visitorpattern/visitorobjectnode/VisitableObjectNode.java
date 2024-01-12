package main.visitorpattern.visitorobjectnode;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface VisitableObjectNode {
    /**
     * This method accepts a visitor to operate on an
     * ItemSelection visitable object
     * @param visitor Visitor
     * @return An Output Object Node
     */
    ObjectNode acceptObjectNode(VisitorObjectNode visitor);
}
