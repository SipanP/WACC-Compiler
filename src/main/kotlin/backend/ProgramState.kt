package backend

import LANGUAGE
import backend.enums.Register
import backend.global.DataDirective
import backend.global.Library
import backend.global.RuntimeErrors
import backend.instruction.GeneralLabel
import java.util.*

class ProgramState {

    /**
     * Static global variables.
     */
    companion object GlobalVals {
        val dataDirective = DataDirective()
        val runtimeErrors = RuntimeErrors(this)
        val library = Library(this)
        var labelNum = 0
    }

    /**
     * Stacks storing the free and used callee saved registers.
     */
    val freeCalleeSavedRegs: ArrayDeque<Register> =
        if (LANGUAGE == Language.ARM) ArrayDeque<Register>(
            listOf(
                Register.R4, Register.R5,
                Register.R6, Register.R7, Register.R8, Register.R9, Register.R10, Register.R11
            )
        ) else ArrayDeque<Register>(
            listOf(
                Register.R7, Register.R8, Register.R9, Register.R10, Register.R11
            )
        )
    val inUseCalleeSavedRegs: ArrayDeque<Register> = ArrayDeque<Register>()

    var accumulatorUsed = false

    /**
     * Free all the callee registers.
     */
    fun freeAllCalleeRegs() {
        while (inUseCalleeSavedRegs.isNotEmpty()) {
            freeCalleeReg()
        }
    }

    /**
     * Free most recently used callee reg.
     */
    fun freeCalleeReg() {
        if (inUseCalleeSavedRegs.isEmpty()) {
            return
        }
        freeCalleeSavedRegs.push(inUseCalleeSavedRegs.pop())
    }

    /**
     * Returns the register at the top of the used callee registers stack.
     * If the accumulator is being used then
     */
    fun recentlyUsedCalleeReg(): Register {
        return if (accumulatorUsed) {
            accumulatorUsed = false
            Register.NONE
        } else {
            inUseCalleeSavedRegs.peek()
        }
    }

    /**
     * Move register from being free to being in used and return this register.
     * If there are no free callee saved registers then set the accumulator to being used and
     * return a NONE register.
     */
    fun getFreeCalleeReg(): Register {
        return if (freeCalleeSavedRegs.isEmpty()) {
            accumulatorUsed = true
            Register.NONE
        } else {
            val reg = freeCalleeSavedRegs.pop()
            inUseCalleeSavedRegs.push(reg)
            reg
        }

    }

    /** Gets the next free label number using a global counter */
    fun getNextLabel(): GeneralLabel {
        return GeneralLabel("L${labelNum++}")
    }
}