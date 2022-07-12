package backend.instruction

import backend.enums.Register

class DivideInstruction(val reg: Register) : Instruction {
    override fun toString(): String {
        return "idiv $reg"
    }
}