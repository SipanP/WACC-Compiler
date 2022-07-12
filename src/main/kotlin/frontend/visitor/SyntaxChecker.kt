package frontend.visitor

import antlr.WACCParser.*
import antlr.WACCParserBaseVisitor
import frontend.errors.SyntaxErrorHandler

class SyntaxChecker(private val syntaxErrorHandler: SyntaxErrorHandler) :
    WACCParserBaseVisitor<Void>() {

    private fun statIsExitOrReturn(stat: StatContext) = (stat is StatSimpleContext) &&
            (stat.EXIT() != null || stat.RETURN() != null)

    private fun ifStatEndsWithExitOrReturn(stat: StatIfContext): Boolean {

        var ifLastStat = stat.stat(0)
        if (ifLastStat is StatMultiContext) {
            ifLastStat = ifLastStat.stat().last()
        }
        var doesEndWithExitOrReturn = statIsExitOrReturn(ifLastStat)
        if (!doesEndWithExitOrReturn && ifLastStat is StatIfContext) {
            doesEndWithExitOrReturn = ifStatEndsWithExitOrReturn(ifLastStat)
        }

        var elseLastStat = stat.stat(1)
        if (elseLastStat is StatMultiContext) {
            elseLastStat = elseLastStat.stat().last()
        }
        if (doesEndWithExitOrReturn) {
            doesEndWithExitOrReturn = if (elseLastStat is StatIfContext) {
                ifStatEndsWithExitOrReturn(elseLastStat)
            } else {
                statIsExitOrReturn(elseLastStat)
            }
        }

        return doesEndWithExitOrReturn
    }

    override fun visitFunc(ctx: FuncContext): Void? {
        var funcStat = ctx.stat()
        if (funcStat is StatMultiContext) {
            funcStat = funcStat.stat().last()
        }
        var endsWithExitOrReturn = statIsExitOrReturn(funcStat)

        if (funcStat is StatIfContext) {
            endsWithExitOrReturn = ifStatEndsWithExitOrReturn(funcStat)
        }

        if (!endsWithExitOrReturn) {
            syntaxErrorHandler.missingExitOrReturnError(ctx)
        }
        return null
    }

    /**
     * The WACC language supports integers in the inclusive range [−2^31, 2^31−1]. This is the same for Java integers.
     * Therefore, the Java NumberFormatException can be used.
     */
    override fun visitIntLiter(ctx: IntLiterContext): Void? {
        try {
            (ctx.text).toInt()
        } catch (e: NumberFormatException) {
            syntaxErrorHandler.intOverflowError(ctx)
        }
        return null
    }

}