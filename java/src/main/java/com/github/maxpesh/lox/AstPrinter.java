package com.github.maxpesh.lox;

class AstPrinter implements Visitor<String> {
    @Override
    public String visitBinaryExpr(Binary expr) {
        return parenthesize(expr.operator().lexeme(), expr.left(), expr.right());
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return parenthesize("group", expr.expression());
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value() == null) return "nil";
        return expr.value().toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return parenthesize(expr.operator().lexeme(), expr.right());
    }

    String print(Expr expr) {
        return expr.accept(this);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Binary(
                new Unary(
                        new Token(TokenType.MINUS, "-", null, 1),
                        new Literal(123)),
                new Token(TokenType.STAR, "*", null, 1),
                new Grouping(new Literal(45.67)));
        System.out.println(new AstPrinter().print(expression));
    }
}
