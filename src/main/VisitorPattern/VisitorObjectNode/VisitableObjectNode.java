package main.VisitorPattern.VisitorObjectNode;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface VisitableObjectNode {
    ObjectNode acceptObjectNode(VisitorObjectNode visitor);
}
