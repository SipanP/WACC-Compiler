package frontend.ast.literal

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ExprAST
import frontend.ast.type.BaseType
import frontend.ast.type.BaseTypeAST
import frontend.ast.type.TypeAST
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a character literal without the quotation marks.
 * E.g. The char literal 'c' has value c
 */
class CharLiterAST(val ctx: ParserRuleContext, val value: Char) : ExprAST(ctx) {
    override fun getType(symbolTable: SymbolTable): TypeAST {
        return BaseTypeAST(ctx, BaseType.CHAR)
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitCharLiterAST(this)
    }
}