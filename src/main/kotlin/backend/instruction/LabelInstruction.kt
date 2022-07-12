package backend.instruction

import LANGUAGE
import backend.Language

abstract class LabelInstruction(val labelName: String) : Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "$labelName:"
            Language.X86_64 -> "$labelName:"
        }
    }
}

class GeneralLabel(labelName: String) : LabelInstruction(labelName)

class FunctionLabel(functionName: String) : LabelInstruction("f_$functionName")

data class MessageLabel(val index: Int, val msg: String) : LabelInstruction(msg) {
    override fun toString(): String {
        val stringInstructions = mutableListOf<String>()
        stringInstructions.add(GeneralLabel("msg_$index").toString())
        if (LANGUAGE == Language.ARM) {
            stringInstructions.add("\t${DirectiveInstruction("word ${msg.length - msg.count { c -> c == '\\' }}")}")
            stringInstructions.add("\t${DirectiveInstruction("ascii \"${msg}\"")}")
        } else {
            stringInstructions.add("\t${DirectiveInstruction("int ${msg.length - msg.count { c -> c == '\\' }}")}")
            stringInstructions.add("\t${DirectiveInstruction("string \"${msg}\"")}")
        }
        return stringInstructions.joinToString(separator = "\n")
    }

}