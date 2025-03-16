package game.Parser;

public class StrategyTokenizer implements Tokenizer {
    private final String src;
    private String next;
    private int pos;

    public StrategyTokenizer(String src) throws Exception {
        this.src = src;
        computeNext();
    }

    public boolean hasNextToken(){
        return next != null;
    }

    public boolean hasTokenLeft(){
        return pos < src.length() - 1;
    }

    public int getPos(){
        return pos;
    }

    @Override
    public void computeNext() throws Exception {
        StringBuilder token = new StringBuilder();
        while (pos < src.length() && (Character.isWhitespace(src.charAt(pos)) || src.charAt(pos) == '#')) {
            if(Character.isWhitespace(src.charAt(pos))) pos++;
            else if(src.charAt(pos) == '#'){
                while(src.charAt(pos) != '\n') pos++;
                pos++;
            }
        }
        if (pos == src.length()) {
            next = null;
            return;
        }
        char c = src.charAt(pos);
        if (Character.isLetter(c)) {
            token.append(src.charAt(pos));
            for (pos++; pos < src.length() && Character.isLetterOrDigit(src.charAt(pos)); pos++) {
                token.append(src.charAt(pos));
            }
        } else if (Character.isDigit(c)) {
            token.append(src.charAt(pos));
            for (pos++; pos < src.length() && Character.isDigit(src.charAt(pos)); pos++) {
                token.append(src.charAt(pos));
            }
        } else if (c == '(' || c == ')' || c == '{' || c == '}' || c == '+'
                || c == '-' || c == '*' || c == '/' || c == '%' || c == '^'
                || c == '=') {
            token.append(c);
            pos++;
        }else throw new Exception(pos + " unknown token: " + c);
        next = token.toString();
    }

    @Override
    public String peek() {
        return next;
    }

    public boolean peek(String str) {
        if (!hasNextToken()) return false;
        return peek().equals(str);
    }

    public void consume(String str) throws Exception {
        if(!hasNextToken()) throw new Exception("No Token Left");
        if(peek().equals(str)) {
            consume();
            return;
        }
        throw new Exception(pos + ", Expected " + str + " but got " + peek());
    }

    @Override
    public String consume() throws Exception {
        String token = next;
        computeNext();
        return token;
    }
}
