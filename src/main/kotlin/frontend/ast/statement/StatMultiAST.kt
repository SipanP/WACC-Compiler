package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing multi-statements.
 */
class StatMultiAST(val ctx: ParserRuleContext, val stats: List<StatAST>) : StatAST(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        for (stat in stats) {
            if (!stat.check(symbolTable)) {
                return false
            }
        }
        return true
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitStatMultiAST(this)
    }
}