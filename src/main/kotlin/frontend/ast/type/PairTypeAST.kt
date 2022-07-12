package frontend.ast.type

import LANGUAGE
import backend.Language
import frontend.SymbolTable
import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a Pair type, e.g. pair (int, bool).
 * typeFst and typeSnd are the types of the first and second element, i.e. int and bool.
 */
class PairTypeAST(ctx: ParserRuleContext, val typeFst: TypeAST, val typeSnd: TypeAST) :
    TypeAST(ctx) {
    override val size = if (LANGUAGE == Language.ARM) 4 else 8

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        return typeFst.check(symbolTable) && typeSnd.check(symbolTable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is ArbitraryTypeAST) return true
        if (javaClass != other?.javaClass) return false

        other as PairTypeAST

        if (typeFst != other.typeFst) return false
        if (typeSnd != other.typeSnd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = typeFst.hashCode()
        result = 31 * result + typeSnd.hashCode()
        return result
    }

    override fun toString(): String {
        return "pair ($typeFst, $typeSnd)"
    }
}