package backend

import backend.instruction.DirectiveInstruction
import backend.instruction.Instruction
import backend.instruction.LabelInstruction

fun printCode(instructions: List<Instruction>): String {
    val code = StringBuilder()
    for (instr in instructions) {
        if (instr.toString() == ".text") {
            code.appendLine()
        }

        if (instr !is DirectiveInstruction && instr !is LabelInstruction ||
            instr.toString() == ".ltorg"
        ) {
            code.append('\t')
        }
        code.appendLine(instr.toString())

        if (instr.toString() == ".data" || instr.toString() == ".text") {
            code.appendLine()
        }
    }
    return code.toString()
}