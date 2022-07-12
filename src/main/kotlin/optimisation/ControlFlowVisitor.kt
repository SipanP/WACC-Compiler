package optimisation

import frontend.ast.ASTNode
import frontend.ast.literal.BoolLiterAST
import frontend.ast.statement.*

class ControlFlowVisitor : OptimisationVisitor() {
    override fun visitIfAST(ast: IfAST): ASTNode {
        if (ast.expr !is BoolLiterAST) {
            return super.visitIfAST(ast)
        }

        val branchStats = if (ast.expr.value) ast.thenStat else ast.elseStat
        val newStats = mutableListOf<StatAST>()

        for (stat in branchStats) {
            newStats.add(visit(stat) as StatAST)
        }
        val newScope = BeginAST(ast.ctx, newStats)
        newScope.symbolTable = if (ast.expr.value) ast.thenSymbolTable else ast.elseSymbolTable
        return newScope
    }

    override fun visitWhileAST(ast: WhileAST): ASTNode {
        if (ast.expr is BoolLiterAST && !ast.expr.value) {
            return SkipAST(ast.ctx)
        }
        return super.visitWhileAST(ast)
    }
}