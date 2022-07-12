package backend.instruction

import LANGUAGE
import backend.Language
import backend.addressingmodes.AddressingMode
import backend.enums.Condition
import backend.enums.Memory
import backend.enums.Register

class LoadInstruction(
    val condition: Condition,
    val addressingMode: AddressingMode,
    val register: Register,
    val memoryType: Memory? = null
) : Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "LDR${memoryType?.name ?: ""}$condition $register, $addressingMode"
            Language.X86_64 -> {
                var result = ""
                if (memoryType == Memory.B || memoryType == Memory.SB) {
                    result += "mov $0, $register\n\t"
                }
                result += "mov${memoryType?.name?.lowercase()?.last() ?: ""} " +
                        "$addressingMode, ${memoryType?.getRegType(register) ?: register}"
                return result
            }

        }
    }
}
