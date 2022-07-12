package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ExprAST
import frontend.ast.type.ArrayTypeAST
import frontend.ast.type.BaseType
import frontend.ast.type.BaseTypeAST
import frontend.ast.type.PairTypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

enum class Command {
    FREE, RETURN, EXIT, PRINT, PRINTLN
}

/**
 * AST node representing simple statements with one command and one expression.
 * Checks that the expression type is compatible with each command.
 */
class StatSimpleAST(val ctx: ParserRuleContext, val command: Command, val expr: ExprAST) :
    StatAST(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        if (!expr.check(symbolTable)) {
            return false
        }
        val exprType = expr.getType(symbolTable)
        if (command == Command.EXIT && (exprType !is BaseTypeAST || exprType.type != BaseType.INT)) {
            semanticErrorHandler.invalidExitType(ctx)
            return false
        }
        if (command == Command.FREE && exprType !is PairTypeAST && exprType !is ArrayTypeAST) {
            semanticErrorHandler.invalidFreeType(ctx)
            return false
        }
        if (command == Command.RETURN) {
            val parentFuncType = symbolTable.funcTypeLookUp()
            if (parentFuncType == null) {
                semanticErrorHandler.invalidReturn(ctx)
                return false
            }
            if (exprType != parentFuncType) {
                semanticErrorHandler.typeMismatch(
                    ctx,
                    parentFuncType.toString(),
                    exprType.toString()
                )
                return false
            }
        }
        return true
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitStatSimpleAST(this)
    }
}
