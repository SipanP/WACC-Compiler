package frontend.ast

import backend.ASTVisitor
import frontend.FuncSymbolTable
import frontend.SymbolTable
import frontend.ast.statement.StatAST
import frontend.ast.type.TypeAST
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a function with the type, identifier, parameters and body.
 * Creates new scope by assigning new symbol table for the function body.
 * Records each parameter in symbol table.
 */
class FuncAST(
    val ctx: ParserRuleContext,
    val type: TypeAST,
    val ident: IdentAST,
    val paramList: List<ParamAST>,
    val stats: List<StatAST>
) : ASTNode(ctx) {
    init {
        symbolTable = FuncSymbolTable(this)
    }

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable.parent = symbolTable
        paramList.forEach {
            this.symbolTable.put(it.ident.name, it)
        }

        stats.forEach {
            if (!it.check(this.symbolTable)) {
                return false
            }
        }
        return ident.check(symbolTable)
    }

    override fun <S : T, T> accept(visitor: ASTVisitor<S>): T? {
        return visitor.visitFuncAST(this)
    }
}
