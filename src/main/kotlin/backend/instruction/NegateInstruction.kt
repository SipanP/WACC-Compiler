package backend.instruction

import backend.enums.Memory
import backend.enums.Register

class NegateInstruction(val reg: Register, val memoryType: Memory) : Instruction {
    override fun toString(): String {
        return "neg ${memoryType.getRegType(reg)}"
    }
}