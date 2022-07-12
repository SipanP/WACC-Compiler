import backend.Language
import frontend.errors.OTHER_ERROR_CODE
import frontend.errors.SUCCESS_CODE
import optimisation.ConstEvalPropVisitor
import optimisation.ControlFlowVisitor
import org.antlr.v4.runtime.CharStreams
import java.io.File
import kotlin.system.exitProcess

var LANGUAGE = Language.ARM

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Error - No WACC file specified.")
        exitProcess(OTHER_ERROR_CODE)
    }
    if (!File(args[0]).exists()) {
        println("Error - ${args[0]} does not exist.")
        exitProcess(OTHER_ERROR_CODE)
    }
    val input = CharStreams.fromFileName(args[0])

    if (args.contains("-x86")) {
        LANGUAGE = Language.X86_64
    }

    // Use frontend to parse input WACC file and output AST with symbol table
    val astStatusPair = frontend.main(input)
    if (astStatusPair.first != SUCCESS_CODE) {
        exitProcess(astStatusPair.first)
    }

    var ast = astStatusPair.second!!

    // Optimise AST by refactoring AST tree and chaining multiple optimisations
    val optimiseAll = args.contains("-o")
    val constEvalProp = optimiseAll || args.contains("-oCEP")
    val controlFlow = optimiseAll || args.contains("-oCF")
    val instrEval = optimiseAll || args.contains("-oIE")

    if (constEvalProp) {
        ast = ConstEvalPropVisitor().visit(ast)
    }
    if (controlFlow) {
        ast = ControlFlowVisitor().visit(ast)
    }

    // Generate assembly instructions by passing into backend
    val code = if (instrEval) backend.optimiseMain(ast) else backend.main(ast)

    // Creates an assembly file and write the instructions
    val fileName = args[0].split(".wacc")[0].split("/").last()
    val file = File("$fileName.s")
    file.writeText(code)

    exitProcess(SUCCESS_CODE)
}