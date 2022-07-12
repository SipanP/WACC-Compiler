package backend.addressingmodes

import LANGUAGE
import backend.Language

class ImmediateLabel(val label: String) : AddressingMode {
    override fun toString(): String {
        return when (LANGUAGE) {
            Language.ARM -> "=$label"
            Language.X86_64 -> label
        }
    }
}