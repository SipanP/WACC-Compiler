package frontend.errors

import org.antlr.v4.runtime.ParserRuleContext

class SemanticErrorHandler : ErrorHandler() {

    override fun errorText(ctx: ParserRuleContext): String {
        return ("Semantic Error ($SEMANTIC_ERROR_CODE)\n - At ${ctx.getStart().line}:${ctx.getStart().charPositionInLine} : ")
    }

    fun inconsistentArrayElem(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Type within array is inconsistent")
    }

    fun invalidAssignment(ctx: ParserRuleContext, from: String, to: String) {
        addErrorWithContext(ctx, "Error assigning $from to $to")
    }

    fun invalidArgNumber(ctx: ParserRuleContext, numOfParams: Int) {
        addErrorWithContext(ctx, "Invalid number of arguments, expecting $numOfParams arguments")
    }

    fun invalidArgType(ctx: ParserRuleContext, argNum: Int, expected: String) {
        addErrorWithContext(ctx, "The $argNum argument has invalid type, expecting $expected")
    }

    fun invalidReadType(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Read can only accept CHAR or INT")
    }

    fun invalidConditional(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Conditional must be of type Bool")
    }

    fun invalidExitType(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Exit code should be of type Int")
    }

    fun invalidFreeType(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Free can only take pair or array type")
    }

    fun invalidReturn(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Return statement is not in a function")
    }

    fun typeMismatch(ctx: ParserRuleContext, expected: String, actual: String) {
        addErrorWithContext(ctx, "Expected $expected, actual $actual")
    }

    fun invalidIndex(ctx: ParserRuleContext) {
        addErrorWithContext(ctx, "Array index dimension doesn't match actual array dimension")
    }

    fun invalidIdentifier(ctx: ParserRuleContext, identName: String) {
        addErrorWithContext(ctx, "Variable $identName not in scope")
    }

    fun alreadyDefined(ctx: ParserRuleContext, identName: String) {
        addErrorWithContext(ctx, "Variable $identName is already defined")
    }

}