package frontend.ast.statement

import backend.ASTVisitor
import frontend.SymbolTable
import frontend.ast.ASTNode
import frontend.ast.FuncAST
import frontend.ast.IdentAST
import frontend.ast.type.TypeAST
import frontend.semanticErrorHandler
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a declare statement for new variables.
 * Checks identifier isn't already defined in local scope.
 * Checks that right-hand side matches type.
 * Records newly declared variable in local symbol table.
 */
class DeclareAST(
    val ctx: ParserRuleContext,
    val type: TypeAST,
    val ident: IdentAST,
    val assignRhs: ASTNode
) : StatAST(ctx) {
    lateinit var label: String

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        val identAST = symbolTable.get(ident.name)
        if (identAST != null && identAST !is FuncAST) {
            semanticErrorHandler.alreadyDefined(ctx, ident.name)
            return false
        }
        if (!assignRhs.check(symbolTable)) {
            return false
        }
        if (assignRhs.getType(symbolTable) != type) {
            semanticErrorHandler.typeMismatch(
                ctx,
                type.toString(),
                assignRhs.getType(symbolTable).toString()
            )
            return false
        }
        symbolTable.put(ident.name, this)
        return true
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitDeclareAST(this)
    }
}
