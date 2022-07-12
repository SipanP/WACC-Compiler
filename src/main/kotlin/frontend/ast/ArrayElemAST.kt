package frontend.ast

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.type.ArrayTypeAST
import frontend.ast.type.TypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing an array element when indexing an array.
 * Checks identifier is of type array and in scope.
 * Checks indexing dimension matches array dimension.
 */
class ArrayElemAST(
    val ctx: ParserRuleContext,
    val ident: IdentAST,
    val listOfIndex: List<ExprAST>
) : ExprAST(ctx) {

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        if (!ident.check(symbolTable)) {
            return false
        }
        val typeAST = ident.getType(symbolTable)
        if (typeAST !is ArrayTypeAST) {
            semanticErrorHandler.typeMismatch(ctx, "ARRAY", typeAST.toString())
            return false
        }
        if (listOfIndex.size != typeAST.dimension) {
            semanticErrorHandler.invalidIndex(ctx)
            return false
        }
        listOfIndex.forEach {
            if (!it.check(symbolTable)) {
                return false
            }
        }
        return true
    }

    override fun getType(symbolTable: SymbolTable): TypeAST {
        val typeAST = ident.getType(symbolTable) as ArrayTypeAST
        return if (typeAST.dimension > listOfIndex.size) {
            ArrayTypeAST(ctx, typeAST.type, typeAST.dimension - listOfIndex.size)
        } else {
            typeAST.type
        }
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitArrayElemAST(this)
    }
}