package frontend.ast.type

import frontend.ast.ASTNode
import org.antlr.v4.runtime.ParserRuleContext

/**
 * TypeAST abstract class is extended by all AST nodes which represent a type.
 * This allows for easier checking of class when checking semantics.
 */
abstract class TypeAST(ctx: ParserRuleContext) : ASTNode(ctx) {
    open val size = 0
}
