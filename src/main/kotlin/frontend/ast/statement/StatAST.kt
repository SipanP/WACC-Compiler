package frontend.ast.statement

import frontend.ast.ASTNode
import org.antlr.v4.runtime.ParserRuleContext

/**
 * Abstract AST node encapsulating all other statement ASTs.
 */
abstract class StatAST(ctx: ParserRuleContext) : ASTNode(ctx)
