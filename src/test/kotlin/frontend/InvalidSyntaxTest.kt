package frontend

import antlr.WACCLexer
import antlr.WACCParser
import frontend.errors.SyntaxErrorHandler
import frontend.errors.SyntaxErrorListener
import frontend.visitor.SyntaxChecker
import getEachFile
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertTrue

class InvalidSyntaxTest {

    /**
     * Tests all the invalid WACC example files with syntax errors, ensuring that at least
     * one syntax error is returned
     */
    @ParameterizedTest
    @MethodSource("testFiles")
    fun invalidFilesReturnSyntaxError(file: File) {

        val errorListener = SyntaxErrorListener()
        val input = CharStreams.fromStream(file.inputStream())
        val lexer = WACCLexer(input)
        lexer.removeErrorListeners()
        lexer.addErrorListener(errorListener)
        val tokens = CommonTokenStream(lexer)

        val parser = WACCParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)
        val tree = parser.program()

        val syntaxErrorHandler = SyntaxErrorHandler()

        val checkSyntaxVisitor = SyntaxChecker(syntaxErrorHandler)
        checkSyntaxVisitor.visit(tree)

        assertTrue(syntaxErrorHandler.hasErrors() || parser.numberOfSyntaxErrors > 0)
    }

    companion object {
        @JvmStatic
        fun testFiles(): List<File> {
            val root = "examples/reference_files/invalid/syntaxErr"
            /**
             * The missingOperand1 file is excluded as a syntax error should no longer be returned
             * thanks to the inclusion of pointer types
             */
            return getEachFile(File(root), listOf(File("$root/expressions/missingOperand1.wacc")))
        }
    }
}