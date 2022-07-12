package backend

import LANGUAGE
import backend.addressingmodes.ImmediateIntOperand
import backend.enums.Register
import backend.instruction.ArithmeticInstrType
import backend.instruction.ArithmeticInstruction
import backend.instruction.Instruction
import frontend.FuncSymbolTable
import frontend.SymbolTable
import frontend.ast.ParamAST
import frontend.ast.statement.DeclareAST

val SIZE_OF_POINTER = if (LANGUAGE == Language.ARM) 4 else 8
private const val MAX_STACK_OFFSET = 1024
private const val X86_64_STACK_ALIGN = 16

/**
 * Calculate the size needed for new declare variables to allocate on stack in this scope
 * @param symbolTable The symbol table of this scope
 * @return The size in bytes of the total declared variables
 */
fun calculateStackOffset(symbolTable: SymbolTable): Int {
    var offset = 0
    var paramSize = 0
    for (astNode in symbolTable.symbolTable.values) {
        if (astNode is DeclareAST) {
            offset += astNode.size()
        } else {
            paramSize += astNode.size()
        }
    }
    symbolTable.currOffset = offset
    // Pad stack in units of 16 bytes for x86_64 to not crash scanf
    if (LANGUAGE == Language.X86_64 && offset % X86_64_STACK_ALIGN != 0) {
        symbolTable.totalDeclaredSize = (offset / X86_64_STACK_ALIGN + 1) * X86_64_STACK_ALIGN
    } else {
        symbolTable.totalDeclaredSize = offset
    }
    symbolTable.totalSize = symbolTable.totalDeclaredSize + paramSize
    return symbolTable.totalDeclaredSize
}

/**
 * Allocate the required size on the stack and add to instructions list
 * @param symbolTable The symbol table of the current scope
 * @param instructions The instructions list to add instructions to
 * @return The size of the stack allocated in bytes
 */
fun allocateStack(symbolTable: SymbolTable, instructions: MutableList<Instruction>): Int {
    val stackOffset = calculateStackOffset(symbolTable)
    moveStackPointer(ArithmeticInstrType.SUB, stackOffset, instructions)
    return stackOffset
}

/**
 * Deallocate the required size on the stack and add to instructions list
 * @param stackOffset The size required to deallocate on the stack
 * @param instructions The instructions list to add instructions to
 * @return The size of the stack deallocated in bytes
 */
fun deallocateStack(stackOffset: Int, instructions: MutableList<Instruction>) {
    moveStackPointer(ArithmeticInstrType.ADD, stackOffset, instructions)
}

/**
 * Helper function to either move the stack pointer up or down
 * @param addOrSubtract Indicate to either increment or decrement stack pointer
 * @param stackOffset The size required to offset on the stack
 * @param instructions The instructions list to add instructions to
 */
fun moveStackPointer(
    addOrSubtract: ArithmeticInstrType, stackOffset: Int,
    instructions: MutableList<Instruction>
) {
    if (stackOffset > 0) {
        var stackOffsetLeft = stackOffset
        while (stackOffsetLeft > MAX_STACK_OFFSET) {
            instructions.add(
                ArithmeticInstruction(
                    addOrSubtract, Register.SP, Register.SP,
                    ImmediateIntOperand(MAX_STACK_OFFSET)
                )
            )
            stackOffsetLeft -= MAX_STACK_OFFSET
        }
        instructions.add(
            ArithmeticInstruction(
                addOrSubtract, Register.SP, Register.SP,
                ImmediateIntOperand(stackOffsetLeft)
            )
        )
    }
}

/**
 * Finds the offset in stack for identifier
 * @param symbolTable The symbolTable of the current scope
 * @param ident The name of the variable
 * @return The offset in the stack for the variable
 */
fun findIdentOffset(symbolTable: SymbolTable, ident: String, accOffset: Int = 0): Int {
    val totalOffset = accOffset + symbolTable.totalSize
    val returnPointerSize = if (LANGUAGE == Language.X86_64) 16 else 4
    var declaredOffset = 0
    var paramOffset = 0
    for ((key, node) in symbolTable.symbolTable.entries.reversed()) {
        if (node is ParamAST) {
            paramOffset += node.size()
            if (key == ident) {
                return totalOffset - paramOffset + returnPointerSize
            }
        } else if (node is DeclareAST) {
            if (key == ident && symbolTable.currOffset <= accOffset + declaredOffset) {
                return accOffset + declaredOffset
            }
            declaredOffset += node.size()
        }
    }
    if (symbolTable.parent != null) {
        /** Searches parent symbol table when not found in current scope.
         * Includes addition of totalOffset size of current scope */
        return findIdentOffset(symbolTable.parent!!, ident, totalOffset)
    }
    return totalOffset
}

/**
 * Helper function to check how much the stack pointer should offset when
 * return statement is reached in a function.
 * @param symbolTable The symbol table of the current scope
 * @return The offset required to move the stack pointer at return statement
 */
fun checkFuncOffset(symbolTable: SymbolTable): Int {
    if (symbolTable is FuncSymbolTable) {
        return symbolTable.totalDeclaredSize
    }
    val offset = symbolTable.symbolTable.values.sumOf { it.size() }
    return checkFuncOffset(symbolTable.parent!!) + offset
}