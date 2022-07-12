package backend.instruction

import LANGUAGE
import backend.Language
import backend.enums.Register

class PushInstruction(private val register: Register) : Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "PUSH {$register}"
            Language.X86_64 -> "pushq $register"
        }
    }
}