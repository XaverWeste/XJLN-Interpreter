package com.github.xjln.interpreter;

public sealed interface AST permits AST.Operation, AST.Call{

    final class Operation implements AST {

    }

    final class Call implements AST{

    }
}
