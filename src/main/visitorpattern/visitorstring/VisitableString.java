package main.visitorpattern.visitorstring;

public interface VisitableString {
    /**
     * This method accepts a visitor to operate on an
     * ItemSelection visitable object
     * @param visitor Visitor
     * @return Any string
     */
    String acceptString(VisitorString visitor);
}
