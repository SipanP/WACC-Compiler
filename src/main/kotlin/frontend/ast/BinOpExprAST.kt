package frontend.ast

import backend.ASTVisitor
import backend.enums.Condition
import frontend.SymbolTable
import frontend.ast.type.BaseType
import frontend.ast.type.BaseTypeAST
import frontend.ast.type.PointerTypeAST
import frontend.ast.type.TypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

interface BinOp

enum class IntBinOp : BinOp {
    PLUS,
    MINUS,
    MULT,
    DIV,
    MOD
}

enum class CmpBinOp(val cond: Condition, val opposite: Condition) : BinOp {
    GTE(Condition.GE, Condition.LT),
    GT(Condition.GT, Condition.LE),
    LTE(Condition.LE, Condition.GT),
    LT(Condition.LT, Condition.GE),
    EQ(Condition.EQ, Condition.NE),
    NEQ(Condition.NE, Condition.EQ)
}

enum class BoolBinOp : BinOp {
    AND,
    OR
}

/**
 * AST node representing a binary operator expression.
 * Checks the type of left and right-hand side expressions match.
 * Checks expression type is compatible with each operator.
 */
class BinOpExprAST(
    val ctx: ParserRuleContext,
    val binOp: BinOp,
    val expr1: ExprAST,
    val expr2: ExprAST
) :
    ExprAST(ctx) {

    // Set to true if first operand is pointer for pointer arithmetic
    var pointerArithmetic = false

    // Shift offset in instruction if pointer arithmetic
    var shiftOffset = 0

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        if (!expr1.check(symbolTable) || !expr2.check(symbolTable)) {
            return false
        }
        val expr1Type = expr1.getType(symbolTable)
        val expr2Type = expr2.getType(symbolTable)

        // Allow for pointer arithmetic
        if (expr1Type is PointerTypeAST && expr2Type is BaseTypeAST && expr2Type.type == BaseType.INT
            && (binOp == IntBinOp.PLUS || binOp == IntBinOp.MINUS)
        ) {
            pointerArithmetic = true
            shiftOffset = when {
                // Char and Bool pointers have a unit of 1 byte so don't need to shift
                expr1Type.type is BaseTypeAST && expr1Type.type.type == BaseType.CHAR -> 0
                expr1Type.type is BaseTypeAST && expr1Type.type.type == BaseType.BOOL -> 0
                else -> 2 // All other types have a unit of 4 bytes, so need to shift by 2
            }
            return true
        }

        if (expr1Type != expr2Type) {
            semanticErrorHandler.typeMismatch(ctx, expr1Type.toString(), expr2Type.toString())
            return false
        }

        return when (binOp) {
            is IntBinOp -> checkInt(expr1Type)
            is CmpBinOp -> checkCmp(expr1Type)
            is BoolBinOp -> checkBool(expr1Type)
            else -> true
        }

    }

    private fun checkInt(type1: TypeAST?): Boolean {
        if (type1 !is BaseTypeAST || type1.type != BaseType.INT) {
            semanticErrorHandler.typeMismatch(ctx, BaseType.INT.toString(), type1.toString())
            return false
        }
        return true
    }

    private fun checkCmp(type1: TypeAST?): Boolean {
        if (binOp == CmpBinOp.LT || binOp == CmpBinOp.GT || binOp == CmpBinOp.LTE || binOp == CmpBinOp.GTE) {
            if (type1 !is BaseTypeAST || type1.type != BaseType.INT && type1.type != BaseType.CHAR) {
                semanticErrorHandler.typeMismatch(ctx, "INT or CHAR", type1.toString())
                return false
            }
        }
        return true
    }

    private fun checkBool(type1: TypeAST?): Boolean {
        if (type1 !is BaseTypeAST || type1.type != BaseType.BOOL) {
            semanticErrorHandler.typeMismatch(ctx, BaseType.BOOL.toString(), type1.toString())
            return false
        }
        return true
    }

    override fun getType(symbolTable: SymbolTable): TypeAST? {
        // Allow pointer arithmetic
        if (pointerArithmetic) {
            return expr1.getType(symbolTable)
        }

        return if (binOp is IntBinOp)
            BaseTypeAST(ctx, BaseType.INT)
        else
            BaseTypeAST(ctx, BaseType.BOOL)
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitBinOpExprAST(this)
    }

}

