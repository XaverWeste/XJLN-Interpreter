package com.github.xjln.interpreter;

public sealed interface AST permits AST.Operation{

    Token execute(Interpreter i);

    final class Operation implements AST {
        public AST.Operation left,right;
        public Token token;

        @Override
        public Token execute(Interpreter i) {
            if(left != null && right != null) return i.executeOperation(left.execute(i), token, right.execute(i));
            if(left == null && right == null) return token;
            throw new RuntimeException("illegal argument");
        }
    }
}
