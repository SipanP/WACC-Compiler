package backend.instruction

import LANGUAGE
import backend.Language
import backend.enums.Condition

class BranchInstruction(val condition: Condition, val label: LabelInstruction, val link: Boolean) :
    Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "B" + (if (link) "L" else "") + condition.toString() + " " + label.labelName
            Language.X86_64 -> {
                if (link) {
                    return "call ${label.labelName}"
                }
                return if (condition == Condition.AL) {
                    "jmp ${label.labelName}"
                } else {
                    "j$condition ${label.labelName}"
                }
            }
        }
    }
}