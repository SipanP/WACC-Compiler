package backend.instruction

import backend.addressingmodes.AddressingMode
import backend.enums.Condition
import backend.enums.Register

class MultiplyInstruction(
    val condition: Condition, val rdLo: Register, val rdHi: Register,
    val rn: Register, val rm: Register, val operand: AddressingMode? = null
) : Instruction {
    override fun toString(): String {
        return "SMULL$condition $rdLo, $rdHi, $rn, $rm"
    }
}

/**
 * Multiples %rax with %{reg}
 */
class IMultiplyInstruction(val reg1: Register, val reg2: Register) : Instruction {
    override fun toString(): String {
        return "imul ${reg1.to32Byte()}, ${reg2.to32Byte()}"
    }
}