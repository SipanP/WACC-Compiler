package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ExprAST
import frontend.ast.type.BaseType
import frontend.ast.type.BaseTypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing an if statement with a then and else block.
 * Creates new scope by assigning new symbol table for then and else block.
 * Checks if condition expression is of type BOOL.
 */
class IfAST(
    val ctx: ParserRuleContext,
    val expr: ExprAST,
    val thenStat: List<StatAST>,
    val elseStat: List<StatAST>
) :
    StatAST(ctx) {
    var thenReturns = false
    var elseReturns = false
    var thenSymbolTable = SymbolTable()
    var elseSymbolTable = SymbolTable()

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        thenSymbolTable.parent = symbolTable
        elseSymbolTable.parent = symbolTable
        if (!expr.check(symbolTable)) {
            return false
        }
        val exprType = expr.getType(symbolTable)
        if (exprType != BaseTypeAST(ctx, BaseType.BOOL)) {
            semanticErrorHandler.invalidConditional(ctx)
            return false
        }
        thenStat.forEach {
            if (!it.check(thenSymbolTable)) {
                return false
            }
        }
        elseStat.forEach {
            if (!it.check(elseSymbolTable)) {
                return false
            }
        }
        return true
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitIfAST(this)
    }
}
