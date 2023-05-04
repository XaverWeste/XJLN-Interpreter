package com.github.xjln.interpreter;

public sealed abstract class AST permits AST.Statement, AST.IfBranch, AST.WhileLoop {

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

    final static class IfBranch extends AST {
        private final Statement condition;
        private final Statement[] statements;
        private final IfBranch otherwise;

        public IfBranch(Statement condition, IfBranch otherwise, Statement...statements){
            this.condition = condition;
            this.statements = statements;
            this.otherwise = otherwise;
        }

        public Statement getCondition(){
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
        private final Statement condition;
        private final Statement[] statements;

        public WhileLoop(Statement condition, Statement...statements){
            this.condition = condition;
            this.statements = statements;
        }

        public Statement getCondition(){
            return condition;
        }

        public Statement[] getStatements(){
            return statements;
        }
    }
}
