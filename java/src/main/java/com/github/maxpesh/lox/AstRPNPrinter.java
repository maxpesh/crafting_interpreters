package com.github.maxpesh.lox;

class AstRPNPrinter implements Visitor<String> {

    @Override
    public String visitBinaryExpr(Binary expr) {
        return expr.left().accept(this) + " " + expr.right().accept(this) + " " + expr.operator().lexeme();
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return expr.expression().accept(this);
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        if (expr.value() == null) {
            return "nil";
        }
        return expr.value().toString();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return expr.operator().lexeme() + expr.right().accept(this);
    }

    private String print(Expr expr) {
        return expr.accept(this);
    }

    public static void main(String[] args) {
        Expr expression = new Binary(
                new Grouping(
                        new Binary(
                                new Unary(new Token(TokenType.MINUS, "-", null, 1), new Literal(1)),
                                new Token(TokenType.PLUS, "+", null, 1),
                                new Literal(2))
                ),
                new Token(TokenType.STAR, "*", null, 1),
                new Grouping(
                        new Binary(
                                new Literal(4),
                                new Token(TokenType.MINUS, "-", null, 1),
                                new Literal(3))
                )
        );
        System.out.println(new AstRPNPrinter().print(expression));
    }
}
