package frontend

import antlr.WACCLexer
import antlr.WACCParser
import frontend.ast.ASTNode
import frontend.errors.*
import frontend.visitor.BuildAST
import frontend.visitor.SyntaxChecker
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CommonTokenStream


val semanticErrorHandler = SemanticErrorHandler()

fun main(input: CharStream): Pair<Int, ASTNode?> {

    val errorListener = SyntaxErrorListener()

    val lexer = WACCLexer(input)
    lexer.removeErrorListeners()
    lexer.addErrorListener(errorListener)
    val tokens = CommonTokenStream(lexer)

    val parser = WACCParser(tokens)
    parser.removeErrorListeners()
    parser.addErrorListener(errorListener)
    val tree = parser.program()

    if (parser.numberOfSyntaxErrors > 0) {
        println("There were ${parser.numberOfSyntaxErrors} syntax errors in the program.")
        return SYNTAX_ERROR_CODE to null
    }

    val syntaxErrorHandler = SyntaxErrorHandler()

    val checkSyntaxVisitor = SyntaxChecker(syntaxErrorHandler)
    checkSyntaxVisitor.visit(tree)

    if (syntaxErrorHandler.hasErrors()) {
        syntaxErrorHandler.printErrors()
        return SYNTAX_ERROR_CODE to null
    }


    val buildASTVisitor = BuildAST()

    val ast = buildASTVisitor.visit(tree)

    val semanticPassed = ast.check(SymbolTable())

    if (semanticErrorHandler.hasErrors() || !semanticPassed) {
        semanticErrorHandler.printErrors()
        return SEMANTIC_ERROR_CODE to null
    }

    return SUCCESS_CODE to ast
}