package frontend.ast

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.type.*
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

enum class UnOp {
    NOT,
    MINUS,
    LEN,
    ORD,
    CHR,
    REF,
    DEREF
}

/**
 * AST node representing an unary operator expression.
 * Checks expression type is compatible with each operator.
 */
class UnOpExprAST(val ctx: ParserRuleContext, val unOp: UnOp, val expr: ExprAST) : ExprAST(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        if (!expr.check(symbolTable)) {
            return false
        }
        val exprType = expr.getType(symbolTable)
        when (unOp) {
            UnOp.NOT -> {
                if (exprType !is BaseTypeAST || exprType.type != BaseType.BOOL) {
                    semanticErrorHandler.typeMismatch(
                        ctx,
                        BaseType.BOOL.toString(),
                        exprType.toString()
                    )
                    return false
                }
            }
            UnOp.MINUS, UnOp.CHR -> {
                if (exprType !is BaseTypeAST || exprType.type != BaseType.INT) {
                    semanticErrorHandler.typeMismatch(
                        ctx,
                        BaseType.INT.toString(),
                        exprType.toString()
                    )
                    return false
                }
            }
            UnOp.LEN -> {
                if (exprType !is ArrayTypeAST) {
                    semanticErrorHandler.typeMismatch(
                        ctx,
                        "ARRAY",
                        expr.getType(symbolTable).toString()
                    )
                    return false
                }
            }
            UnOp.ORD -> {
                if (exprType !is BaseTypeAST || exprType.type != BaseType.CHAR) {
                    semanticErrorHandler.typeMismatch(
                        ctx,
                        BaseType.CHAR.toString(),
                        exprType.toString()
                    )
                    return false
                }
            }
            UnOp.REF -> {
                if (expr !is IdentAST && expr !is ArrayElemAST) {
                    semanticErrorHandler.typeMismatch(
                        ctx,
                        "Variable or Array element",
                        exprType.toString()
                    )
                    return false
                }
            }
            UnOp.DEREF -> {
                if (exprType !is PointerTypeAST) {
                    semanticErrorHandler.typeMismatch(
                        ctx,
                        "Pointer",
                        exprType.toString()
                    )
                    return false
                }
            }
        }
        return true
    }

    override fun getType(symbolTable: SymbolTable): TypeAST {
        return when (unOp) {
            UnOp.NOT -> BaseTypeAST(ctx, BaseType.BOOL)
            UnOp.CHR -> BaseTypeAST(ctx, BaseType.CHAR)
            UnOp.MINUS, UnOp.LEN, UnOp.ORD -> BaseTypeAST(ctx, BaseType.INT)
            UnOp.REF -> PointerTypeAST(ctx, expr.getType(symbolTable)!!)
            UnOp.DEREF -> (expr.getType(symbolTable) as PointerTypeAST).type
        }
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitUnOpExprAST(this)
    }
}

