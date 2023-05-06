package com.github.xjln.interpreter;

public sealed abstract class AST permits AST.Statement, AST.IfBranch, AST.WhileLoop, AST.Calculation, AST.Call {

    sealed static class Statement extends AST permits AST.ReturnStatement {
        public final String statement;

        public Statement(String statement){
            this.statement = statement;
        }
    }

    final static class ReturnStatement extends Statement {

        public ReturnStatement(String statement){
            super(statement);
        }
    }

    final static class Call extends AST{
        //TODO
    }

    final static class Calculation extends AST{
        public Calculation left, right;
        public Token token;

        public Token execute(Interpreter i) {
            //if(left != null && right != null) return i.executeOperation(left.execute(i), token, right.execute(i)); TODO
            if(left == null && right == null) return token;
            throw new RuntimeException("illegal argument");
        }
    }

    final static class IfBranch extends AST {
        private final Calculation condition;
        private final Statement[] statements;
        private final IfBranch otherwise;

        public IfBranch(Calculation condition, IfBranch otherwise, Statement...statements){
            this.condition = condition;
            this.statements = statements;
            this.otherwise = otherwise;
        }

        public Calculation getCondition(){
            return condition;
        }

        public Statement[] getStatements(){
            return statements;
        }

        public IfBranch getOtherwise(){
            return otherwise;
        }
    }

    final static class WhileLoop extends AST {
        private final Calculation condition;
        private final Statement[] statements;

        public WhileLoop(Calculation condition, Statement...statements){
            this.condition = condition;
            this.statements = statements;
        }

        public Calculation getCondition(){
            return condition;
        }

        public Statement[] getStatements(){
            return statements;
        }
    }
}
