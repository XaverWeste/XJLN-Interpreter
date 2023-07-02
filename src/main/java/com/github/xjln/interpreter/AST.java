package com.github.xjln.interpreter;

public sealed abstract class AST permits AST.Calc, AST.If, AST.MethodCall, AST.ObjCre, AST.RefCall, AST.VarCre, AST.While {

    final static class Calc extends AST{
        public Calc left = null, right = null;
        public Token content = null;
        public AST call = null;
    }

    final static class MethodCall extends AST{
        public String name;
        public Calc[] args;
    }

    final static class RefCall extends AST{
        public String name = null;
        public RefCall next = null;
        public MethodCall call = null;
    }

    final static class VarCre extends AST{

    }

    final static class ObjCre extends AST{

    }

    final static class If extends AST{

    }

    final static class While extends AST{
        public Calc condition;
        public AST[] content;
    }
}
