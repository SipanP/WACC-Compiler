package frontend

import antlr.WACCLexer
import antlr.WACCParser
import frontend.errors.SyntaxErrorHandler
import frontend.errors.SyntaxErrorListener
import frontend.visitor.BuildAST
import frontend.visitor.SyntaxChecker
import getEachFile
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import kotlin.test.assertFalse

class InvalidSemanticTest {

    /**
     * Tests all the invalid WACC example files with semantic errors, ensuring that at least
     * one semantic error is returned, with no syntax errors
     */
    @ParameterizedTest
    @MethodSource("testFiles")
    fun invalidFilesReturnSemanticError(file: File) {
        var newErrorCount = semanticErrorHandler.errorCount()
        var failedTest = false

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

        if (syntaxErrorHandler.hasErrors() || parser.numberOfSyntaxErrors > 0) {
            failedTest = true
        } else {
            val buildASTVisitor = BuildAST()

            val ast = buildASTVisitor.visit(tree)

            ast.check(SymbolTable())

            val oldErrorCount = newErrorCount
            newErrorCount = semanticErrorHandler.errorCount()

            if (newErrorCount - oldErrorCount == 0) {
                failedTest = true
            }
        }
        assertFalse(failedTest)
    }

    companion object {
        @JvmStatic
        fun testFiles(): List<File> {
            return getEachFile(File("examples/reference_files/invalid/semanticErr"))
        }
    }
}