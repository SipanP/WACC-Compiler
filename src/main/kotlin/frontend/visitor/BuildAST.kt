package frontend.visitor

import antlr.WACCParser
import antlr.WACCParserBaseVisitor
import frontend.ast.*
import frontend.ast.literal.*
import frontend.ast.statement.*
import frontend.ast.type.*


class BuildAST : WACCParserBaseVisitor<ASTNode>() {


    override fun visitProgram(ctx: WACCParser.ProgramContext): ASTNode {
        val funcList = mutableListOf<FuncAST>()
        for (func in ctx.func()) {
            funcList.add(visit(func) as FuncAST)
        }
        val stat = visit(ctx.stat()) as StatAST
        return if (stat is StatMultiAST) {
            ProgramAST(ctx, funcList, stat.stats)
        } else {
            ProgramAST(ctx, funcList, mutableListOf(stat))
        }
    }

    override fun visitFunc(ctx: WACCParser.FuncContext): ASTNode {
        val paramList = mutableListOf<ParamAST>()
        if (ctx.paramList() != null) {
            for (param in ctx.paramList().param()) {
                paramList.add(visit(param) as ParamAST)
            }
        }
        val ident = visit(ctx.ident()) as IdentAST
        val stat = visit(ctx.stat()) as StatAST
        return if (stat is StatMultiAST) {
            FuncAST(ctx, visit(ctx.type()) as TypeAST, ident, paramList, stat.stats)
        } else {
            FuncAST(ctx, visit(ctx.type()) as TypeAST, ident, paramList, mutableListOf(stat))
        }
    }

    override fun visitParam(ctx: WACCParser.ParamContext): ASTNode {
        return ParamAST(ctx, visit(ctx.type()) as TypeAST, visit(ctx.ident()) as IdentAST)
    }

    override fun visitAssignRhs(ctx: WACCParser.AssignRhsContext): ASTNode {
        if (ctx.NEWPAIR() != null) {
            return NewPairAST(
                ctx,
                visit(ctx.expr(0)) as ExprAST,
                visit(ctx.expr(1)) as ExprAST
            )
        }
        if (ctx.CALL() != null) {
            val argList = mutableListOf<ExprAST>()
            if (ctx.argList() != null) {
                for (expr in ctx.argList().expr()) {
                    argList.add(visit(expr) as ExprAST)
                }
            }
            return CallAST(ctx, visit(ctx.ident()) as IdentAST, argList)
        }
        return visitChildren(ctx)
    }

    override fun visitStatWhile(ctx: WACCParser.StatWhileContext): ASTNode {
        val ctxStat = visit(ctx.stat()) as StatAST
        return if (ctxStat is StatMultiAST) {
            WhileAST(
                ctx,
                visit(ctx.expr()) as ExprAST,
                ctxStat.stats
            )
        } else {
            WhileAST(
                ctx,
                visit(ctx.expr()) as ExprAST,
                mutableListOf(ctxStat)
            )
        }
    }

    override fun visitStatRead(ctx: WACCParser.StatReadContext): ASTNode {
        return ReadAST(ctx, visit(ctx.assignLhs()))
    }

    override fun visitStatDeclare(ctx: WACCParser.StatDeclareContext): ASTNode {
        return DeclareAST(
            ctx,
            visit(ctx.type()) as TypeAST,
            visit(ctx.ident()) as IdentAST,
            visit(ctx.assignRhs())
        )
    }

    override fun visitStatAssign(ctx: WACCParser.StatAssignContext): ASTNode {
        return AssignAST(ctx, visit(ctx.assignLhs()), visit(ctx.assignRhs()))
    }

    override fun visitStatIf(ctx: WACCParser.StatIfContext): ASTNode {
        return IfAST(
            ctx,
            visit(ctx.expr()) as ExprAST,
            mutableListOf(visit(ctx.stat(0)) as StatAST),
            mutableListOf(visit(ctx.stat(1)) as StatAST)
        )
    }

    override fun visitStatMulti(ctx: WACCParser.StatMultiContext): ASTNode {
        val statFirst = visit(ctx.stat(0)) as StatAST
        val statSecond = visit(ctx.stat(1)) as StatAST
        return if (statFirst is StatMultiAST) {
            StatMultiAST(ctx, statFirst.stats + statSecond)
        } else {
            StatMultiAST(ctx, mutableListOf(statFirst, statSecond))
        }
    }

    override fun visitStatSimple(ctx: WACCParser.StatSimpleContext): ASTNode {
        val command = when {
            ctx.FREE() != null -> Command.FREE
            ctx.RETURN() != null -> Command.RETURN
            ctx.EXIT() != null -> Command.EXIT
            ctx.PRINT() != null -> Command.PRINT
            ctx.PRINTLN() != null -> Command.PRINTLN
            else -> throw RuntimeException()
        }
        return StatSimpleAST(ctx, command, visit(ctx.expr()) as ExprAST)
    }

    override fun visitStatSkip(ctx: WACCParser.StatSkipContext): ASTNode {
        return SkipAST(ctx)
    }


    override fun visitPairElem(ctx: WACCParser.PairElemContext): ASTNode {
        val index = when {
            ctx.FST() != null -> PairIndex.FST
            ctx.SND() != null -> PairIndex.SND
            else -> throw RuntimeException()
        }
        return PairElemAST(ctx, index, visit(ctx.expr()) as ExprAST)
    }

    override fun visitBaseType(ctx: WACCParser.BaseTypeContext): ASTNode {
        val baseType = when {
            ctx.INT_T() != null -> BaseType.INT
            ctx.BOOL_T() != null -> BaseType.BOOL
            ctx.CHAR_T() != null -> BaseType.CHAR
            ctx.STRING_T() != null -> BaseType.STRING
            else -> throw RuntimeException()
        }
        return BaseTypeAST(ctx, baseType)
    }

    override fun visitArrayType(ctx: WACCParser.ArrayTypeContext): ASTNode {
        return if (ctx.pointerType() == null) {
            ArrayTypeAST(ctx, visit(ctx.getChild(0)) as TypeAST, ctx.L_BRACKET().size)
        } else {
            ArrayTypeAST(ctx, visit(ctx.pointerType()) as PointerTypeAST, ctx.L_BRACKET().size)
        }
    }

    override fun visitPairType(ctx: WACCParser.PairTypeContext): ASTNode {
        return PairTypeAST(
            ctx, visit(ctx.pairElemType(0)) as TypeAST,
            visit(ctx.pairElemType(1)) as TypeAST
        )
    }

    override fun visitPairElemType(ctx: WACCParser.PairElemTypeContext): ASTNode {
        return if (ctx.PAIR() != null) {
            ArbitraryTypeAST(ctx)
        } else {
            visitChildren(ctx)
        }
    }

    override fun visitExprBinOp(ctx: WACCParser.ExprBinOpContext): ASTNode {
        val binOp = when (ctx.getChild(1).text) {
            "+" -> IntBinOp.PLUS
            "-" -> IntBinOp.MINUS
            "*" -> IntBinOp.MULT
            "/" -> IntBinOp.DIV
            "%" -> IntBinOp.MOD
            ">=" -> CmpBinOp.GTE
            ">" -> CmpBinOp.GT
            "<=" -> CmpBinOp.LTE
            "<" -> CmpBinOp.LT
            "==" -> CmpBinOp.EQ
            "!=" -> CmpBinOp.NEQ
            "&&" -> BoolBinOp.AND
            "||" -> BoolBinOp.OR
            else -> throw RuntimeException()
        }
        return BinOpExprAST(
            ctx,
            binOp as BinOp,
            visit(ctx.expr(0)) as ExprAST,
            visit(ctx.expr(1)) as ExprAST
        )
    }

    override fun visitExprUnOp(ctx: WACCParser.ExprUnOpContext): ASTNode {
        val unOp = when {
            ctx.unaryOper().NOT() != null -> UnOp.NOT
            ctx.unaryOper().MINUS() != null -> UnOp.MINUS
            ctx.unaryOper().LEN() != null -> UnOp.LEN
            ctx.unaryOper().ORD() != null -> UnOp.ORD
            ctx.unaryOper().CHR() != null -> UnOp.CHR
            ctx.unaryOper().REF() != null -> UnOp.REF
            ctx.unaryOper().MULT() != null -> UnOp.DEREF
            else -> throw RuntimeException()
        }

        return UnOpExprAST(ctx, unOp, visit(ctx.expr()) as ExprAST)
    }

    override fun visitExprBrackets(ctx: WACCParser.ExprBracketsContext): ASTNode {
        return visit(ctx.expr())
    }

    override fun visitArrayElem(ctx: WACCParser.ArrayElemContext): ASTNode {
        val listOfIndex = mutableListOf<ExprAST>()
        for (expr in ctx.expr()) {
            listOfIndex.add(visit(expr) as ExprAST)
        }
        return ArrayElemAST(
            ctx,
            visit(ctx.ident()) as IdentAST,
            listOfIndex
        )
    }

    override fun visitArrayLiter(ctx: WACCParser.ArrayLiterContext): ASTNode {
        val vals = mutableListOf<ExprAST>()
        for (expr in ctx.expr()) {
            vals.add(visit(expr) as ExprAST)
        }
        return ArrayLiterAST(ctx, vals)
    }

    override fun visitPairLiter(ctx: WACCParser.PairLiterContext): ASTNode {
        return NullPairLiterAST(ctx)
    }

    override fun visitBoolLiter(ctx: WACCParser.BoolLiterContext): ASTNode {
        return when {
            ctx.TRUE() != null -> BoolLiterAST(ctx, true)
            ctx.FALSE() != null -> BoolLiterAST(ctx, false)
            else -> throw RuntimeException()
        }
    }

    override fun visitIntLiter(ctx: WACCParser.IntLiterContext): ASTNode {
        return IntLiterAST(ctx, Integer.parseInt(ctx.text))
    }

    override fun visitStrLiter(ctx: WACCParser.StrLiterContext): ASTNode {
        return StrLiterAST(ctx, ctx.text.substring(1, ctx.text.length - 1))
    }

    override fun visitCharLiter(ctx: WACCParser.CharLiterContext): ASTNode {
        val str = ctx.text.substring(1, ctx.text.length - 1)
            .replace("\\0", 0.toChar().toString())
            .replace("\\b", "\b")
            .replace("\\t", "\t")
            .replace("\\n", "\n")
            .replace("\\f", 12.toChar().toString())
            .replace("\\r", "\r")
            .replace("\\\"", "\"")
            .replace("\\\'", "\'")
            .replace("\\\\", "\\")
        return CharLiterAST(ctx, str[0])
    }

    override fun visitIdent(ctx: WACCParser.IdentContext): ASTNode {
        return IdentAST(ctx, ctx.text)
    }

    override fun visitStatBegin(ctx: WACCParser.StatBeginContext): ASTNode {
        val ctxStat = visit(ctx.stat()) as StatAST
        return if (ctxStat is StatMultiAST) {
            BeginAST(
                ctx,
                ctxStat.stats
            )
        } else {
            BeginAST(
                ctx,
                mutableListOf(ctxStat)
            )
        }
    }

    override fun visitPointerElem(ctx: WACCParser.PointerElemContext): ASTNode {
        return PointerElemAST(ctx, visit(ctx.expr()) as ExprAST)
    }

    override fun visitPointerType(ctx: WACCParser.PointerTypeContext): ASTNode {
        var type = visit(ctx.getChild(0)) as TypeAST
        for (i in 1..ctx.MULT().size) {
            type = PointerTypeAST(ctx, type)
        }
        return type
    }
}