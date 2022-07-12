package backend.instruction

import backend.enums.Memory

class SignExtendInstruction(val from: Memory) : Instruction {
    override fun toString(): String {
        return when (from) {
            Memory.B, Memory.SB -> "cbw"
            Memory.W -> "cwde"
            Memory.L -> "cdqe"
            Memory.Q -> "cqo"
        }
    }

}