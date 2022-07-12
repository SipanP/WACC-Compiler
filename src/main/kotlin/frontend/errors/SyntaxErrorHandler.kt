package frontend.errors

import org.antlr.v4.runtime.ParserRuleContext

class SyntaxErrorHandler : ErrorHandler() {

    override fun errorText(ctx: ParserRuleContext): String {
        return ("Syntax Error ($SYNTAX_ERROR_CODE)\n - At ${ctx.getStart().line}:${ctx.getStart().charPositionInLine} : ")
    }

    fun missingExitOrReturnError(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Function does not end with an exit or return statement")
    }

    fun intOverflowError(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Int assignment overflow")
    }


}