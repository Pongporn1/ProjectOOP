package Parser;

import java.util.HashSet;
import java.util.Set;
import GameState.Game.Direction;
import AST.*;

public class StrategyParser implements Parser<Strategy> {

    final Set<String> reservedWord = new HashSet<String>(){{
        add("ally"); add("done"); add("down"); add("downleft"); add("downright"); add("else");
        add("if"); add("move"); add("nearby"); add("opponent"); add("shoot"); add("then");
        add("up"); add("upleft"); add("upright"); add("while");
    }};
    StrategyTokenizer tokenizer;

    public StrategyParser(StrategyTokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public StrategyParser(String script) throws Exception {
        this(new StrategyTokenizer(script));
    }

    @Override
    public Strategy parse() throws Exception {
        return parseStrategy();
    }

    public Strategy parseStrategy() throws Exception {
        Strategy s = new Strategy(parseStatement());
        while (tokenizer.hasNextToken()) {
            s.addStatement(parseStatement());
        }
        return s;
    }

    public Statement parseStatement() throws Exception {
        Statement s;
        if (tokenizer.peek("if")) {
            s = parseIfStatement();
        } else if (tokenizer.peek("while")) {
            s = parseWhileStatement();
        } else if (tokenizer.peek("{")) {
            s = parseBlockStatement();
        } else {
            s = parseCommand();
        }
        return s;

    }

    public Statement parseCommand() throws Exception {
        Statement s;
        if (tokenizer.peek("done") || tokenizer.peek("move") || tokenizer.peek("shoot")) {
            s = parseActionCommand();
        } else {
            s = parseAssignmentStatement();
        }
        return s;
    }

    public Statement parseBlockStatement() throws Exception {
        BlockStatement blockStatement = new BlockStatement();
        tokenizer.consume("{");
        while (!tokenizer.peek("}")) blockStatement.addStatement(parseStatement());
        tokenizer.consume("}");
        return blockStatement;
    }

    public Statement parseIfStatement() throws Exception {
        tokenizer.consume("if");
        tokenizer.consume("(");
        Expression condition = parseExpression();
        tokenizer.consume(")");
        tokenizer.consume("then");
        Statement trueStatement = parseStatement();
        tokenizer.consume("else");
        Statement falseStatement = parseStatement();
        return new IfStatement(condition, trueStatement, falseStatement);
    }

    public Statement parseWhileStatement() throws Exception {
        tokenizer.consume("while");
        tokenizer.consume("(");
        Expression condition = parseExpression();
        tokenizer.consume(")");
        Statement statement = parseStatement();
        return new WhileStatement(statement, condition);
    }

    public Statement parseAssignmentStatement() throws Exception {
        String var = tokenizer.consume();
        tokenizer.consume("=");
        return new AssignmentStatement(var, parseExpression());
    }

    public Statement parseActionCommand() throws Exception {
        if (tokenizer.peek("done")) {
            tokenizer.consume("done");
            return new DoneCommand();
        } else if (tokenizer.peek("move")) {
            return parseMoveCommand();
        } else if (tokenizer.peek("shoot")) {
            return parseAttackCommand();
        } else throw new Exception();
    }

    public Statement parseMoveCommand() throws Exception {
        tokenizer.consume();
        return new MoveCommand(parseDirection());
    }

    public Statement parseAttackCommand() throws Exception {
        tokenizer.consume();
        return new AttackCommand(parseDirection(), parseExpression());
    }

    public Direction parseDirection() throws Exception {
        String dir = tokenizer.consume();
        return switch (dir) {
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            case "upleft" -> Direction.UP_LEFT;
            case "upright" -> Direction.UP_RIGHT;
            case "downleft" -> Direction.DOWN_LEFT;
            case "downright" -> Direction.DOWN_RIGHT;
            default -> throw new Exception("Unknown token " + dir +". Invalid direction");
        };
    }

    public Expression parseInfoExpression() throws Exception {
        if (tokenizer.peek("ally")) {
            tokenizer.consume();
            return new AllyExpression();
        } else if (tokenizer.peek("opponent")) {
            tokenizer.consume();
            return new OpponentExpression();
        } else if (tokenizer.peek("nearby")) {
            tokenizer.consume();
            return new NearbyExpression(parseDirection());
        } else throw new Exception();
    }

    private boolean isNumber(String token) {
        if(token.isEmpty()) return false;
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) return false;
        }
        return true;
    }

    private boolean isValidIdentifier(String token) {
        if(token.isEmpty() || Character.isDigit(token.charAt(0))) return false;
        return !reservedWord.contains(token);
    }


    public Expression parsePower() throws Exception {
        Expression e;
        if (tokenizer.peek("(")) {
            tokenizer.consume("(");
            e = parseExpression();
            tokenizer.consume(")");
        } else if (isNumber(tokenizer.peek())) {
            e = new IntegerLiteral(Integer.parseInt(tokenizer.consume()));
        } else if (tokenizer.peek("ally") || tokenizer.peek("opponent") || tokenizer.peek("nearby")) {
            e = parseInfoExpression();
        } else if (isValidIdentifier(tokenizer.peek())){
            e = new Identifier(tokenizer.consume());
        }else throw new Exception("Unknown token for parse power: " + tokenizer.peek() + " " + tokenizer.getPos());
        return e;
    }

    public Expression parseFactor() throws Exception {
        Expression e = parsePower();
        while (tokenizer.peek("^")) {
            e = new RightAssociativeBinaryExpression(tokenizer.consume(), e, parsePower());
        }
        return e;
    }

    public Expression parseTerm() throws Exception {
        Expression e = parseFactor();
        while (tokenizer.peek("*") || tokenizer.peek("/") || tokenizer.peek("%")) {
            e = new LeftAssociativeBinaryExpression(tokenizer.consume(), e, parseFactor());
        }
        return e;
    }

    public Expression parseExpression() throws Exception {
        Expression e = parseTerm();
        while (tokenizer.peek("+") || tokenizer.peek("-")) {
            e = new LeftAssociativeBinaryExpression(tokenizer.consume(), e, parseTerm());
        }
        return e;
    }
}
