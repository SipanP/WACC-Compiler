package backend.instruction

import LANGUAGE
import backend.Language
import backend.addressingmodes.AddressingMode
import backend.enums.Condition
import backend.enums.Register

class MoveInstruction(val condition: Condition, val reg: Register, val value: AddressingMode) :
    Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "MOV$condition $reg, $value"
            Language.X86_64 -> "mov $value, $reg"
        }
    }

}


class CMoveInstruction(val condition: Condition, val reg1: Register, val reg2: Register) :
    Instruction {
    override fun toString(): String {
        return "cmov$condition $reg1, $reg2"
    }

}