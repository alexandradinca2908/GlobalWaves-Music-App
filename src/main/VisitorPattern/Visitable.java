package main.VisitorPattern;

public interface Visitable {
    String accept(Visitor visitor);
}
