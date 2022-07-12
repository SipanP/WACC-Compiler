package frontend.ast

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.type.PairTypeAST
import frontend.ast.type.TypeAST
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a new pair holding the first and second field.
 */
class NewPairAST(val ctx: ParserRuleContext, val fst: ExprAST, val snd: ExprAST) : ASTNode(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        return (fst.check(symbolTable) && snd.check(symbolTable))
    }

    override fun getType(symbolTable: SymbolTable): TypeAST {
        return PairTypeAST(ctx, fst.getType(symbolTable)!!, snd.getType(symbolTable)!!)
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitNewPairAST(this)
    }
}