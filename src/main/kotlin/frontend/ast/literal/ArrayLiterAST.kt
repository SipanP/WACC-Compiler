package frontend.ast.literal

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ASTNode
import frontend.ast.ExprAST
import frontend.ast.type.ArbitraryTypeAST
import frontend.ast.type.ArrayTypeAST
import frontend.ast.type.TypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node that is used for instantiating new arrays.
 * vals represents the elements in the array.
 * E.g. [1, 3, 9, 102]
 */
class ArrayLiterAST(val ctx: ParserRuleContext, val vals: List<ExprAST>) : ASTNode(ctx) {
    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        for (elem in vals) {
            if (!elem.check(symbolTable)) {
                return false
            }
        }
        if (vals.isNotEmpty()) {
            val elemType = vals[0].getType(symbolTable)
            for (elem in vals) {
                if (elem.getType(symbolTable) != elemType) {
                    semanticErrorHandler.inconsistentArrayElem(ctx)
                    return false
                }
            }
        }
        return true
    }

    override fun getType(symbolTable: SymbolTable): TypeAST {
        return if (vals.isEmpty()) {
            ArrayTypeAST(ctx, ArbitraryTypeAST(ctx), 1)
        } else {
            val exprType = vals[0].getType(symbolTable)
            if (exprType is ArrayTypeAST) {
                ArrayTypeAST(ctx, exprType.type, exprType.dimension + 1)
            } else {
                ArrayTypeAST(ctx, exprType!!, 1)
            }
        }
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitArrayLiterAST(this)
    }
}
