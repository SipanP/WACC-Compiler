package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ASTNode
import frontend.ast.ExprAST
import frontend.ast.type.BaseType
import frontend.ast.type.BaseTypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a while statement with a condition expression and a body.
 * Creates new scope by assigning new symbol table for the body block.
 * Checks condition expression is of type BOOL.
 */
class WhileAST(val ctx: ParserRuleContext, val expr: ExprAST, val stats: List<ASTNode>) :
    StatAST(ctx) {
    var bodySymbolTable = SymbolTable()

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        bodySymbolTable.parent = symbolTable
        if (!expr.check(symbolTable)) {
            return false
        }
        val exprType = expr.getType(symbolTable)
        if (exprType !is BaseTypeAST || exprType.type != BaseType.BOOL) {
            semanticErrorHandler.invalidConditional(ctx)
            return false
        }
        stats.forEach {
            if (!it.check(bodySymbolTable)) {
                return false
            }
        }
        return true
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitWhileAST(this)
    }
}