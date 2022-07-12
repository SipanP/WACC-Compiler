package frontend

import antlr.WACCLexer
import antlr.WACCParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class ParserTest {
    /**
     * Runs the lexer and parser on the provided input, ensuring the final tree
     * matches with the expectation
     */
    private fun checkParserOutput(expectedTree: String, input: String) {
        val charInput = CharStreams.fromString(input)
        val lexer = WACCLexer(charInput)
        val tokens = CommonTokenStream(lexer)
        val parser = WACCParser(tokens)
        val tree = parser.program()
        assertEquals(expectedTree, tree.toStringTree(parser))
    }

    @Test
    fun parserReturnsBasicProgramTreeWithSkip() {
        val expectedTree = "(program begin (stat skip) end <EOF>)"
        checkParserOutput(expectedTree, "begin skip end")
    }

    @Test
    fun parserReturnsPrintTree() {
        val expectedTree =
            "(program begin (stat print (expr (strLiter \"lorem ipsum\"))) end <EOF>)"
        checkParserOutput(expectedTree, "begin print \"lorem ipsum\" end")
    }

    @Test
    fun parserReturnsAssignmentTree() {
        val expectedTree = "(program begin (stat (type (baseType int)) (ident i) = " +
                "(assignRhs (expr (intLiter 10)))) end <EOF>)"
        checkParserOutput(expectedTree, "begin int i = 10 end")
    }

    @Test
    fun parserReturnsSequenceTree() {
        val expectedTree = "(program begin (stat (stat (type (baseType int)) (ident i) = " +
                "(assignRhs (expr (intLiter 10)))) ; (stat print (expr (strLiter \"lorem ipsum\")))) end <EOF>)"
        checkParserOutput(
            expectedTree, "begin\n" +
                    "    int i = 10;\n" +
                    "    print \"lorem ipsum\"\n" +
                    "end"
        )
    }

    @Test
    fun parserReturnsCorrectOrderOfOperationsTree() {
        val expectedTree = "(program begin (stat (type (baseType int)) (ident i) = (assignRhs" +
                " (expr (expr (expr (intLiter 10)) (binaryOper2 +) (expr " +
                "(expr (intLiter 5)) (binaryOper1 *) (expr (intLiter 6)))) (binaryOper2 -) " +
                "(expr (intLiter 2))))) end <EOF>)"
        checkParserOutput(
            expectedTree, "begin\n" +
                    "    int i = 10 + 5 * 6 - 2\n" +
                    "end"
        )
    }
}