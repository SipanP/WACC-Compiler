package frontend.ast

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.statement.DeclareAST
import frontend.ast.type.TypeAST
import org.antlr.v4.runtime.ParserRuleContext

/**
 * Abstract AST node encapsulating all AST nodes
 */
abstract class ASTNode(ctx: ParserRuleContext) {
    open var symbolTable = SymbolTable()

    open fun check(symbolTable: SymbolTable): Boolean {
        return true
    }

    open fun getType(symbolTable: SymbolTable): TypeAST? {
        return null
    }

    open fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return null
    }

    fun size(): Int {
        return when (this) {
            is DeclareAST -> this.type.size
            is FuncAST -> this.type.size
            is ParamAST -> this.type.size
            else -> 0
        }
    }

}