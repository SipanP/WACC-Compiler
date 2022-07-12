package frontend.ast.type

import org.antlr.v4.runtime.ParserRuleContext

/**
 * AST node representing a pair without types for its element.
 * It can either represent null, Pair (pair, pair) or the type of the empty array.
 */
class ArbitraryTypeAST(ctx: ParserRuleContext) : TypeAST(ctx) {
    override val size = 4

    override fun equals(other: Any?): Boolean {
        return other is ArbitraryTypeAST || other is PairTypeAST || other is PointerTypeAST
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "ArbitraryTypeAST"
    }
}