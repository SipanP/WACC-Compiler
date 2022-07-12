package backend.instruction

import LANGUAGE
import backend.Language
import backend.addressingmodes.AddressingMode
import backend.enums.Register

enum class LogicOperation {
    AND,
    ORR,
    EOR;

    override fun toString(): String {
        return when (LANGUAGE) {
            Language.X86_64 -> {
                when (this) {
                    AND -> "and"
                    ORR -> "or"
                    EOR -> "xor"
                }
            }
            Language.ARM -> this.name
        }
    }
}

class LogicInstruction(
    val op: LogicOperation, val reg1: Register, val reg2: Register,
    val operand: AddressingMode
) : Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "$op $reg1, $reg2, $operand"
            Language.X86_64 -> {
                var result = ""
                if (reg1 != reg2) {
                    result += "mov $reg2, $reg1\n\t"
                }
                result += "$op $operand, $reg1"
                return result
            }
        }
    }
}