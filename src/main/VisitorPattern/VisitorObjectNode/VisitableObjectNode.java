package main.VisitorPattern.VisitorObjectNode;

import com.fasterxml.jackson.databind.node.ObjectNode;
import main.VisitorPattern.VisitorString.VisitorString;

public interface VisitableObjectNode {
    ObjectNode acceptObjectNode(VisitorObjectNode visitor);
}
