package frontend.ast

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.literal.NullPairLiterAST
import frontend.ast.type.PairTypeAST
import frontend.ast.type.TypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

enum class PairIndex {
    FST,
    SND
}

/**
 * AST node representing a pair element when indexing pair.
 * Checks expression is of pair type.
 */
class PairElemAST(val ctx: ParserRuleContext, val index: PairIndex, val expr: ExprAST) :
    ASTNode(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        if (!expr.check(symbolTable)) {
            return false
        }
        if (expr is NullPairLiterAST) {
            semanticErrorHandler.typeMismatch(ctx, "PAIR", "NULL")
            return false
        }
        if (expr !is IdentAST || expr.getType(symbolTable) !is PairTypeAST) {
            semanticErrorHandler.typeMismatch(ctx, "PAIR", expr.getType(symbolTable).toString())
            return false
        }
        return true
    }

    override fun getType(symbolTable: SymbolTable): TypeAST? {
        val elemType = expr.getType(symbolTable)
        return if (elemType is PairTypeAST) {
            when (index) {
                PairIndex.FST -> elemType.typeFst
                PairIndex.SND -> elemType.typeSnd
            }
        } else {
            semanticErrorHandler.typeMismatch(ctx, "PAIR", elemType.toString())
            null
        }
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitPairElemAST(this)
    }
}

