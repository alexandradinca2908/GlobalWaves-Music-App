package main.VisitorPattern;

public interface Visitable {
    public float accept(Visitor visitor);
}
