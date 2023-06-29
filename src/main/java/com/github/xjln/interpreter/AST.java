package com.github.xjln.interpreter;

public sealed abstract class AST permits AST.Statement, AST.IfBranch, AST.WhileLoop, AST.Calculation, AST.Call {

    sealed static class Statement extends AST permits AST.ReturnStatement {
        public final Statement next;
        public final Token[] token;
        public final Call call;

        public Statement(Statement next, Token[] token){
            this.next = next;
            this.token = token;
            call = null;
        }

        public Statement(Statement next, Call call){
            this.next = next;
            token = null;
            this.call = call;
        }
    }

    final static class ReturnStatement extends Statement {

        public ReturnStatement(Statement next, Token[] token){
            super(next, token);
        }

        public ReturnStatement(Statement next, Call call){
            super(next, call);
        }
    }

    final static class Call extends AST{
        public final String call;
        public final Call next;
        public final Calculation[] args;

        public Call(String call, Call next, Calculation[] args){
            this.call = call;
            this.next = next;
            this.args = args;
        }
    }

    final static class Calculation extends AST{
        public Calculation left, right;
        public Token token;
        public Call call;

        public Token execute(Interpreter i) {
            //if(left != null && right != null) return i.executeOperation(left.execute(i), token, right.execute(i)); TODO
            if(left == null && right == null){
                if(token != null) return token;
                else return null; //TODO
            }
            throw new RuntimeException("illegal argument");
        }
    }

    final static class IfBranch extends AST {
        public final Calculation condition;
        public final Statement[] statements;
        public final IfBranch otherwise;

        public IfBranch(Calculation condition, IfBranch otherwise, Statement...statements){
            this.condition = condition;
            this.statements = statements;
            this.otherwise = otherwise;
        }
    }

    final static class WhileLoop extends AST {
        public final Calculation condition;
        public final Statement[] statements;

        public WhileLoop(Calculation condition, Statement...statements){
            this.condition = condition;
            this.statements = statements;
        }
    }
}
