package main.VisitorPattern;

public interface Visitable {
    public String accept(Visitor visitor);
}
