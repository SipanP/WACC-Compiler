package backend

import frontend.ast.ASTNode
import frontend.ast.ProgramAST
import optimisation.InstrEval

fun main(ast: ASTNode): String {
    val instructions = GenerateASTVisitor(ProgramState()).visit(ast as ProgramAST)
    return printCode(instructions)
}

fun optimiseMain(ast: ASTNode): String {
    val instructions = GenerateASTVisitor(ProgramState()).visit(ast as ProgramAST)

    /**
     * Runs instruction evaluation on the generated assembly code if optimisation is enabled
     */

    val optimisedInstr = InstrEval().optimise(instructions)

    return printCode(optimisedInstr)
}