package backend.global

import LANGUAGE
import backend.Language
import backend.instruction.DirectiveInstruction
import backend.instruction.Instruction
import backend.instruction.MessageLabel

/**
 * Generate, store and translate all directives with labels for the .data directive.
 */
class DataDirective {
    private val dataLabels = mutableListOf<String>()

    fun addStringLabel(stringLabel: String): String {
        var output = ""
        if (LANGUAGE == Language.X86_64) {
            output = "$"
        }
        output += if (dataLabels.contains(stringLabel)) {
            "msg_${dataLabels.indexOf(stringLabel)}"
        } else {
            dataLabels.add(stringLabel)
            "msg_${dataLabels.size - 1}"
        }
        return output
    }

    fun toStringLabel(string: String): String {
        var output = ""
        if (LANGUAGE == Language.X86_64) {
            output = "$"
        }
        return output + "msg_${dataLabels.indexOf(string)}"
    }

    fun translate(): List<Instruction> {
        if (dataLabels.isEmpty()) {
            return emptyList()
        }

        val instructions = mutableListOf<Instruction>(DirectiveInstruction("data"))
        for ((index, label) in dataLabels.withIndex()) {
            instructions.add(MessageLabel(index, label))
        }
        return instructions
    }
}