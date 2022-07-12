package frontend.ast

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.statement.DeclareAST
import frontend.ast.type.ArrayTypeAST
import frontend.ast.type.PairTypeAST
import frontend.ast.type.TypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing an identifier.
 * Checks the identifier is in scope.
 */
class IdentAST(val ctx: ParserRuleContext, val name: String) : ExprAST(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        if (symbolTable.identLookUp(name) == null) {
            semanticErrorHandler.invalidIdentifier(ctx, name)
            return false
        }
        return true
    }

    override fun getType(symbolTable: SymbolTable): TypeAST {
        return when (val type = symbolTable.lookupAll(name)) {
            is DeclareAST -> type.type
            is FuncAST -> type.type
            is ParamAST -> type.type
            is ArrayTypeAST -> type
            is PairTypeAST -> type
            else -> throw RuntimeException("Undefined variable $name. Symbol table lost!")
        }
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitIdentAST(this)
    }
}
