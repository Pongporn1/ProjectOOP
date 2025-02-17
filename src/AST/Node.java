package AST;

public interface Node {
    default void prettyPrint(StringBuilder s) {
        prettyPrint(s, "");
    }
    void prettyPrint(StringBuilder s, String prefix);
}
