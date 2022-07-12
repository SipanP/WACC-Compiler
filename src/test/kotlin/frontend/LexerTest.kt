package frontend

import antlr.WACCLexer
import org.antlr.v4.runtime.CharStreams
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LexerTest {
    /**
     * Runs the lexer on the provided input, ensuring each token matches
     * with the expectation
     */
    private fun checkLexerOutput(input: String, tokens: List<String>) {
        val charInput = CharStreams.fromString(input)
        val lexer = WACCLexer(charInput)
        for (token in tokens) {
            assertEquals(token, lexer.nextToken().text)
        }
    }

    @Test
    fun lexerReturnsBasicProgramTokens() {
        val tokens = mutableListOf("begin", "skip", "end")
        checkLexerOutput("begin skip end", tokens)
    }

    @Test
    fun lexerReturnsStringTokens() {
        val tokens = mutableListOf("begin", "\"lorem ipsum\"", "end")
        checkLexerOutput("begin \"lorem ipsum\" end", tokens)
    }

    @Test
    fun lexerReturnsAssignmentTokens() {
        val tokens = mutableListOf("begin", "int", "a", "=", "10", "end")
        checkLexerOutput("begin int a = 10 end", tokens)
    }

    @Test
    fun lexerReturnsBooleanTokens() {
        val tokens = mutableListOf("begin", "true", "&&", "false", "||", "true", "end")
        checkLexerOutput("begin true && false || true end", tokens)
    }

    @Test
    fun lexerReturnsConditionalTokens() {
        val tokens = mutableListOf("begin", "if", "true", "then", "else", "fi", "end")
        checkLexerOutput("begin if true then else fi end", tokens)
    }

    @Test
    fun lexerIgnoresWhitespace() {
        val tokens = mutableListOf("begin", "int", "a", "=", "10", "end")
        checkLexerOutput("  begin  \n  int  a \t        =   \r  10        end", tokens)
    }

    @Test
    fun lexerIgnoresComments() {
        val tokens = mutableListOf("begin", "end")
        checkLexerOutput("begin #COMMENT \n end", tokens)
    }
}