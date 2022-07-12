package backend.addressingmodes

import LANGUAGE
import backend.Language
import backend.enums.Register

class RegisterOperand(val register: Register) : AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> register.toString()
            Language.X86_64 -> register.toString()
        }
    }
}

class RegisterOperandWithShift(val register: Register, val shiftType: ShiftType, val offset: Int) :
    AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "$register, $shiftType #$offset"
            Language.X86_64 -> "$shiftType $$offset, $register"
        }
    }
}

enum class ShiftType {
    ASR,
    LSL;

    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> name
            Language.X86_64 -> {
                when (this) {
                    ASR -> "shr"
                    LSL -> "shl"
                }
            }
        }
    }
}