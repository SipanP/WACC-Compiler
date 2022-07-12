package backend.instruction

import LANGUAGE
import backend.Language
import backend.addressingmodes.AddressingMode
import backend.enums.Memory
import backend.enums.Register

class StoreInstruction(val mode: AddressingMode, val reg: Register, val memory: Memory? = null) :
    Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "STR${memory?.name ?: ""} $reg, $mode"
            Language.X86_64 -> "mov${memory?.name?.lowercase()?.last() ?: ""} ${
                memory?.getRegType(
                    reg
                ) ?: reg
            }, " +
                    "$mode"
        }
    }
}