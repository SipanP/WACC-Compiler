package frontend.ast.statement

import backend.ASTVisitor
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a skip statement
 */
class SkipAST(ctx: ParserRuleContext) : StatAST(ctx) {

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitSkipAST(this)
    }
}
