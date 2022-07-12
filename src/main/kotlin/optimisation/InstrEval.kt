package optimisation

import backend.addressingmodes.ImmediateInt
import backend.addressingmodes.RegisterOperand
import backend.instruction.*

class InstrEval {
    fun optimise(instructions: List<Instruction>): List<Instruction> {
        val optimised = mutableListOf<Instruction>()

        var prev = instructions.first()
        for (i in instructions) {
            if (!(storeThenLoad(prev, i) || addZero(prev, i) || moveSameReg(i))) {
                optimised.add(i)
            }
            prev = i
        }
        return optimised
    }

    private fun storeThenLoad(prev: Instruction, curr: Instruction): Boolean {
        return prev is StoreInstruction && curr is LoadInstruction && prev.reg.toString() == curr.register.toString()
                && prev.mode.toString() == curr.addressingMode.toString()
    }

    private fun addZero(prev: Instruction, curr: Instruction): Boolean {
        return prev is LoadInstruction && prev.addressingMode is ImmediateInt && prev.addressingMode.num == 0
                && curr is ArithmeticInstruction && curr.type == ArithmeticInstrType.ADD && curr.operand is RegisterOperand
                && curr.operand.toString() == prev.register.toString() && curr.reg1 == curr.reg2
    }

    private fun moveSameReg(curr: Instruction): Boolean {
        return curr is MoveInstruction && curr.value is RegisterOperand && curr.value.register.toString() == curr.reg.toString()
    }

}