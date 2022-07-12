package frontend.ast.literal

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ExprAST
import frontend.ast.type.ArbitraryTypeAST
import frontend.ast.type.TypeAST
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing 'null'
 */
class NullPairLiterAST(val ctx: ParserRuleContext) : ExprAST(ctx) {

    override fun getType(symbolTable: SymbolTable): TypeAST {
        return ArbitraryTypeAST(ctx)
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitNullPairLiterAST(this)
    }
}