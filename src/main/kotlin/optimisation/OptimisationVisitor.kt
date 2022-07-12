package optimisation

import backend.ASTVisitor
import frontend.ast.*
import frontend.ast.literal.*
import frontend.ast.statement.*

abstract class OptimisationVisitor : ASTVisitor<ASTNode> {
    override fun visitProgramAST(ast: ProgramAST): ASTNode {
        val funcList = mutableListOf<FuncAST>()
        for (func in ast.funcList) {
            funcList.add(visit(func) as FuncAST)
        }
        val stats = mutableListOf<StatAST>()
        for (stat in ast.stats) {
            stats.add(visit(stat) as StatAST)
        }
        val program = ProgramAST(ast.ctx, funcList, stats)
        program.symbolTable = ast.symbolTable
        return program
    }

    override fun visitFuncAST(ast: FuncAST): ASTNode {
        val stats = mutableListOf<StatAST>()
        for (stat in ast.stats) {
            stats.add(visit(stat) as StatAST)
        }
        val function = FuncAST(ast.ctx, ast.type, ast.ident, ast.paramList, stats)
        function.symbolTable = ast.symbolTable
        return function
    }

    override fun visitBeginAST(ast: BeginAST): ASTNode {
        val stats = mutableListOf<StatAST>()
        for (stat in ast.stats) {
            stats.add(visit(stat) as StatAST)
        }
        val begin = BeginAST(ast.ctx, stats)
        begin.symbolTable = ast.symbolTable
        return begin
    }

    override fun visitIfAST(ast: IfAST): ASTNode {
        val condition = visit(ast.expr) as ExprAST
        val thenStats = mutableListOf<StatAST>()
        val elseStats = mutableListOf<StatAST>()
        for (stat in ast.thenStat) {
            thenStats.add(visit(stat) as StatAST)
        }
        for (stat in ast.elseStat) {
            elseStats.add(visit(stat) as StatAST)
        }

        val ifAST = IfAST(ast.ctx, condition, thenStats, elseStats)
        ifAST.symbolTable = ast.symbolTable
        ifAST.thenSymbolTable = ast.thenSymbolTable
        ifAST.elseSymbolTable = ast.elseSymbolTable
        ifAST.thenReturns = ast.thenReturns
        ifAST.elseReturns = ast.elseReturns
        return ifAST
    }

    override fun visitWhileAST(ast: WhileAST): ASTNode {
        val condition = visit(ast.expr) as ExprAST
        val stats = mutableListOf<StatAST>()
        for (stat in ast.stats) {
            stats.add(visit(stat) as StatAST)
        }

        val whileAST = WhileAST(ast.ctx, condition, stats)
        whileAST.symbolTable = ast.symbolTable
        whileAST.bodySymbolTable = ast.bodySymbolTable
        return whileAST
    }

    override fun visitParamAST(ast: ParamAST): ASTNode {
        return ast
    }

    override fun visitBinOpExprAST(ast: BinOpExprAST): ASTNode {
        return ast
    }

    override fun visitUnOpExprAST(ast: UnOpExprAST): ASTNode {
        return ast
    }

    override fun visitIdentAST(ast: IdentAST): ASTNode {
        return ast
    }

    override fun visitPairElemAST(ast: PairElemAST): ASTNode {
        return ast
    }

    override fun visitNewPairAST(ast: NewPairAST): ASTNode {
        return ast
    }

    override fun visitArrayElemAST(ast: ArrayElemAST): ASTNode {
        return ast
    }

    override fun visitAssignAST(ast: AssignAST): ASTNode {
        return ast
    }

    override fun visitCallAST(ast: CallAST): ASTNode {
        return ast
    }

    override fun visitDeclareAST(ast: DeclareAST): ASTNode {
        return ast
    }

    override fun visitReadAST(ast: ReadAST): ASTNode {
        return ast
    }

    override fun visitSkipAST(ast: SkipAST): ASTNode {
        return ast
    }

    override fun visitStatMultiAST(ast: StatMultiAST): ASTNode {
        return ast
    }

    override fun visitStatSimpleAST(ast: StatSimpleAST): ASTNode {
        return ast
    }

    override fun visitIntLiterAST(ast: IntLiterAST): ASTNode {
        return ast
    }

    override fun visitBoolLiterAST(ast: BoolLiterAST): ASTNode {
        return ast
    }

    override fun visitStrLiterAST(ast: StrLiterAST): ASTNode {
        return ast
    }

    override fun visitCharLiterAST(ast: CharLiterAST): ASTNode {
        return ast
    }

    override fun visitNullPairLiterAST(ast: NullPairLiterAST): ASTNode {
        return ast
    }

    override fun visitArrayLiterAST(ast: ArrayLiterAST): ASTNode {
        return ast
    }

    override fun visitPointerElemAST(ast: PointerElemAST): ASTNode {
        return ast
    }
}