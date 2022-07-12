package backend.instruction

import LANGUAGE
import backend.Language
import backend.enums.Register

class PopInstruction(private val register: Register) : Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "POP {$register}"
            Language.X86_64 -> "popq $register"
        }
    }
}