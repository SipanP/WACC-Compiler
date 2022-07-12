package frontend.ast.type

import LANGUAGE
import backend.Language
import frontend.SymbolTable
import org.antlr.v4.runtime.ParserRuleContext
import java.util.*

/**
 * AST node representing an array type e.g. int[][]. The type of the lowest level elements and the dimension
 * are passed into the constructor. For example, int[][] will have type int and dimension 2.
 */
class ArrayTypeAST(ctx: ParserRuleContext, val type: TypeAST, val dimension: Int) : TypeAST(ctx) {
    override val size = if (LANGUAGE == Language.ARM) 4 else 8

    override fun check(symbolTable: SymbolTable): Boolean {
        this.symbolTable = symbolTable
        return type.check(symbolTable)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayTypeAST

        if (type is ArbitraryTypeAST || other.type is ArbitraryTypeAST) return true
        if (type != other.type) return false
        if (dimension != other.dimension) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(type, dimension)
    }

    override fun toString(): String {
        val arrayString = StringBuilder()
        arrayString.append(type.toString())
        for (i in 1..dimension) {
            arrayString.append("[]")
        }
        return arrayString.toString()
    }
}