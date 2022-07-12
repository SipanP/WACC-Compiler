package backend

import frontend.ast.*
import frontend.ast.literal.*
import frontend.ast.statement.*

interface ASTVisitor<T> {

    fun visit(ast: ASTNode): T {
        return ast.accept(this)!!
    }

    /**
     * Visit program and function ASTs.
     */
    fun visitProgramAST(ast: ProgramAST): T
    fun visitFuncAST(ast: FuncAST): T
    fun visitParamAST(ast: ParamAST): T

    /**
     * Visit expression ASTs.
     */
    fun visitBinOpExprAST(ast: BinOpExprAST): T
    fun visitUnOpExprAST(ast: UnOpExprAST): T

    /**
     * Visit ident AST.
     */
    fun visitIdentAST(ast: IdentAST): T

    /**
     * Visit Pair and Array ASTs.
     */
    fun visitPairElemAST(ast: PairElemAST): T
    fun visitNewPairAST(ast: NewPairAST): T
    fun visitArrayElemAST(ast: ArrayElemAST): T

    /**
     * Visit statement ASTs.
     */
    fun visitAssignAST(ast: AssignAST): T
    fun visitBeginAST(ast: BeginAST): T
    fun visitCallAST(ast: CallAST): T
    fun visitDeclareAST(ast: DeclareAST): T
    fun visitIfAST(ast: IfAST): T
    fun visitReadAST(ast: ReadAST): T
    fun visitSkipAST(ast: SkipAST): T
    fun visitWhileAST(ast: WhileAST): T
    fun visitStatMultiAST(ast: StatMultiAST): T
    fun visitStatSimpleAST(ast: StatSimpleAST): T

    /**
     * Visit literal ASTs.
     */
    fun visitIntLiterAST(ast: IntLiterAST): T
    fun visitBoolLiterAST(ast: BoolLiterAST): T
    fun visitStrLiterAST(ast: StrLiterAST): T
    fun visitCharLiterAST(ast: CharLiterAST): T
    fun visitNullPairLiterAST(ast: NullPairLiterAST): T
    fun visitArrayLiterAST(ast: ArrayLiterAST): T

    /**
     * Visit Type Elem AST
     */
    fun visitPointerElemAST(ast: PointerElemAST): T
}