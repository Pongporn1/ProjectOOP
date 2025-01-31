package MinionStrategy;

public interface Tokenizer {
    public void computeNext() throws Exception;

    public String peek();

    public String consume() throws Exception;
}
