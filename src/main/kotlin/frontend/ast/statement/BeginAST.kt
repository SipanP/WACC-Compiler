package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a begin end block.
 * Creates new scope by assigning new symbol table.
 */
class BeginAST(val ctx: ParserRuleContext, val stats: List<StatAST>) : StatAST(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = SymbolTable()
        this.symbolTable.parent = symbolTable
        for (stat in stats) {
            if (!stat.check(this.symbolTable)) {
                return false
            }
        }
        return true
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitBeginAST(this)
    }
}
