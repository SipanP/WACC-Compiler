package backend.instruction

import LANGUAGE
import backend.Language

class DirectiveInstruction(private val directive: String) : Instruction {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> ".$directive"
            Language.X86_64 -> ".$directive"
        }
    }
}