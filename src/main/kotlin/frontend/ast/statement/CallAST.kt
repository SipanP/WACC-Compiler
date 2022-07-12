package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ExprAST
import frontend.ast.FuncAST
import frontend.ast.IdentAST
import frontend.ast.type.TypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a call function statement.
 * Checks identifier is a function and in scope.
 * Checks number of arguments match required parameters for that function.
 * Checks each argument matches required type for each parameter.
 */
class CallAST(val ctx: ParserRuleContext, val ident: IdentAST, val args: List<ExprAST>) :
    StatAST(ctx) {

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        val function = symbolTable.lookupAll(ident.name)
        if (function == null || function !is FuncAST) {
            semanticErrorHandler.invalidIdentifier(ctx, ident.name)
            return false
        }
        if (function.paramList.size != args.size) {
            semanticErrorHandler.invalidArgNumber(ctx, function.paramList.size)
            return false
        }
        for (i in args.indices) {
            if (!args[i].check(symbolTable)) {
                return false
            }
            val argType = args[i].getType(symbolTable)
            if (argType != function.paramList[i].type) {
                semanticErrorHandler.invalidArgType(ctx, i, function.paramList[i].type.toString())
                return false
            }
        }
        return true
    }

    override fun getType(symbolTable: SymbolTable): TypeAST {
        return ident.getType(symbolTable)
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitCallAST(this)
    }
}