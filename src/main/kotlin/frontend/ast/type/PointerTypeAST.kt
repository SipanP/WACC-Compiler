package frontend.ast.type

import frontend.SymbolTable
import org.antlr.v4.runtime.ParserRuleContext

class PointerTypeAST(val ctx: ParserRuleContext, val type: TypeAST) : TypeAST(ctx) {
    override val size = 4

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        return type.check(symbolTable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is ArbitraryTypeAST) return true
        return other is PointerTypeAST && type == other.type
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

    override fun toString(): String {
        return "$type*"
    }
}
