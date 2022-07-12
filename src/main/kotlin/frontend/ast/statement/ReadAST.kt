package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ASTNode
import frontend.ast.type.BaseType
import frontend.ast.type.BaseTypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a read statement that stores input to variable.
 * Checks variable is either of type CHAR or INT.
 */
class ReadAST(val ctx: ParserRuleContext, val assignLhs: ASTNode) : StatAST(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        if (!assignLhs.check(symbolTable)) {
            return false
        }
        if (assignLhs.getType(symbolTable) != BaseTypeAST(ctx, BaseType.CHAR) &&
            assignLhs.getType(symbolTable) != BaseTypeAST(ctx, BaseType.INT)
        ) {
            semanticErrorHandler.invalidReadType(ctx)
            return false
        }
        return true
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitReadAST(this)
    }
}
