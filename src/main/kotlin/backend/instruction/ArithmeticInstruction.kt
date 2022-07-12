package backend.instruction

import LANGUAGE
import backend.Language
import backend.addressingmodes.AddressingMode
import backend.enums.Register

enum class ArithmeticInstrType {
    ADD,
    SUB,
    RSB;

    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> name
            Language.X86_64 -> name.lowercase()
        }
    }
}


class ArithmeticInstruction(
    val type: ArithmeticInstrType,
    val reg1: Register,
    val reg2: Register,
    var operand: AddressingMode,
    val update: Boolean = false,
    val shifted: Register? = null,
    val intCalc: Boolean = false
) : Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "$type${if (update) "S" else ""} $reg1, $reg2, $operand"
            Language.X86_64 -> {
                var result = ""
                if (shifted != null && !intCalc) {
                    result += "$operand\n\t"
                }
                if (type == ArithmeticInstrType.RSB) {
                    return "neg ${reg1.to32Byte()}"
                }
                if (reg1 != reg2) {
                    result += "mov $reg2, $reg1\n\t"
                }
                result += if (shifted == null) {
                    "$type $operand, $reg1"
                } else {
                    // Must use shifted reg due to call to .to32Byte()
                    if (intCalc) {
                        "$type${if (intCalc) "l" else ""} ${if (intCalc) shifted.to32Byte() else operand}, ${reg1.to32Byte()}"
                    } else {
                        "$type $shifted, $reg1"
                    }
                }
                return result
            }

        }

    }
}