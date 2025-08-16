/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import expressivo.parser.ExpressionLexer;
import expressivo.parser.ExpressionListener;
import expressivo.parser.ExpressionParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   non-negative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS3 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {

    static void main(String[] args) {
        String input = "(2*x    )+    (    y*x    )";

    }
    
    // Datatype definition
    //   Expression =
    //      Number(value: double) +
    //      Variable(name: String) +
    //      Addition(left: Expression, right: Expression) +
    //      Multiplication(left: Expression, rightL: Expression)
    
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS3 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    static Expression parse(String input) {
        try {
            CharStream stream = new ANTLRInputStream(input);
            ExpressionLexer lexer = new ExpressionLexer(stream);
            lexer.reportErrorsAsExceptions();

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExpressionParser parser = new ExpressionParser(tokens);
            parser.reportErrorsAsExceptions();

            ParseTree tree = parser.root();
            ParseTreeWalker walker = new ParseTreeWalker();

            MakeExpression expMaker = new MakeExpression();
            walker.walk(expMaker, tree);
            return expMaker.getExpression();
        } catch (RecognitionException e) {
            // ANTLR parsing error
            throw new IllegalArgumentException("Parse failed: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            // others
            throw new IllegalArgumentException("Parse failed: " + e.getMessage(), e);
        }
    }

    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override
    String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS3 handout.
     */
    @Override
    boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    int hashCode();

    // factory methods
    /**
     * Create a variable expression.
     *
     * @param var variable name; must be a nonempty string of one or more case-sensitive letters (a-z, A-Z)
     * @return a Variable expression representing the variable named var
     */
    static Expression make(String var) {
        return new Variable(var);
    }

    /**
     * Create a numeric constant expression.
     *
     * @param val a non-negative number (integer or floating-point)
     * @return a Number expression representing the constant value val
     */
    static Expression make(double val) {
        return new Number(val);
    }

    /**
     * Create an addition expression: left + right.
     * The order of left and right matters.
     *
     * @param left  left operand (must not be null)
     * @param right right operand (must not be null)
     * @return an Addition expression representing (left + right)
     */
    static Expression add(Expression left, Expression right) {
        return new Addition(left, right);
    }

    /**
     * Create a multiplication expression: left * right.
     * The order of left and right matters.
     *
     * @param left  left operand (must not be null)
     * @param right right operand (must not be null)
     * @return a Multiplication expression representing (left * right)
     */
    static Expression multiply(Expression left, Expression right) {
        return new Multiplication(left, right);
    }



    // TODO more instance methods
    public Expression differentiate(String variable);
    
}

/** Make an Expression from a parse tree. */
class MakeExpression implements ExpressionListener {
    private final Stack<Expression> stack = new Stack<>();

    /**
     * Return the expression constructed by this listener object.
     * @return Expression for the parse tree that was walked
     */
    public Expression getExpression() {
        return stack.peek();
    }

    @Override
    public void enterRoot(ExpressionParser.RootContext ctx) {

    }

    @Override
    public void exitRoot(ExpressionParser.RootContext ctx) {

    }

    @Override
    public void enterExpr(ExpressionParser.ExprContext ctx) {

    }

    @Override
    public void exitExpr(ExpressionParser.ExprContext ctx) {
        int n = ctx.term().size();
        List<Expression> terms = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            terms.add(0, stack.pop());
        }
        Expression sum = terms.get(0);
        for (int i = 1; i < n; ++i) {
            sum = Expression.add(sum, terms.get(i));
        }
        stack.push(sum);
    }

    @Override
    public void enterTerm(ExpressionParser.TermContext ctx) {

    }

    @Override
    public void exitTerm(ExpressionParser.TermContext ctx) {
        int n = ctx.factor().size();
        List<Expression> factors = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            factors.add(0, stack.pop());
        }
        Expression product = factors.get(0);
        for (int i = 1; i < n; ++i) {
            product = Expression.multiply(product, factors.get(i));
        }
        stack.push(product);
    }

    @Override
    public void enterFactor(ExpressionParser.FactorContext ctx) {

    }

    @Override
    public void exitFactor(ExpressionParser.FactorContext ctx) {
        if (ctx.NUMBER() != null) {
            double number = Double.parseDouble(ctx.NUMBER().getText());
            stack.push(Expression.make(number));
        } else if (ctx.VARIABLE() != null) {
            String var = ctx.VARIABLE().getText();
            stack.push(Expression.make(var));
        } else {
            // matched the '(' expr ')' alternative
            // do nothing
        }
    }

    @Override
    public void visitTerminal(TerminalNode terminalNode) {

    }

    @Override
    public void visitErrorNode(ErrorNode errorNode) {

    }

    @Override
    public void enterEveryRule(ParserRuleContext parserRuleContext) {

    }

    @Override
    public void exitEveryRule(ParserRuleContext parserRuleContext) {

    }
}