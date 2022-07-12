package frontend.ast

import org.antlr.v4.runtime.ParserRuleContext

/**
 * Abstract AST node encapsulating all expression nodes.
 */
abstract class ExprAST(ctx: ParserRuleContext) : ASTNode(ctx)