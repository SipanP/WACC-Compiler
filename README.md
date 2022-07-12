# WACC Compiler

This group project was developed for the Imperial College London COMP50006 Compilers module.

For this group project we implemented a compiler that converts WACC code into either ARM or x86 assembly code, using Kotlin and ANTLR.

A more detailed [final report](doc/report.pdf) can be found in the `/doc` folder.

## WACC

WACC (read 'whack') is a basic variant on the While family of languages found in many program reasoning courses. It has most of the common language features you would expect of such a language (i.e. variables, expressions, branching & looping)

Imperial College London provides a [reference compiler](https://teaching.doc.ic.ac.uk/wacc_compiler/) for the WACC language

## Structure

- ANTLR files perform Lexical and Syntactical Analysis on the provided file
- Semantic Analysis is performed on resulting tokens to generate an Abstract Syntax Tree (AST)
- Optionally, optimisations are performed by traversing this AST
- Finally, Code Generation produces an assembly file of the same name 

Our compiler has a very modular design, with multiple intermediate representations of the code. This enables thorough testing of each, as well as extension, with an AST that can be quickly traversed. 

## Optimisations

- **Constant Evaluation**: Any binary/unary expression containing only literals will be evaluated and a single literal AST Node returned, avoiding the need to push constant on to the stack.
- **Constant Propagation**: When a new variable is declared, we recursively evaluate the expression assigned to it to return the literal AST Node the expression represents.
- **Control Flow Analysis**: The AST is analysed for `if true`, `if false` and `while false` conditional branch statements and then simplified to just the branch that would be executed.
- **Instruction Evaluation**: The generated code is inspected and redundant instructions are removed (i.e adding 0 to a register or moving the contents of a register to itself).

## Using the Compiler 

First, use the provided Makefile to build the compiler:

    $ make 

Our compiler can then produce assembly code for both ARM ([ARM1176JZF-S](https://developer.arm.com/documentation/ddi0301/h)) and x86 ([x86_64](https://www.intel.com/content/www/us/en/developer/articles/technical/intel-sdm.html)) architectures with a variety of optimisations:

```
$ compile [FLAGS] <FILE>

FILE:
    fileName.wacc

FLAGS:
    -x86    Produces x86 assembly code as output
            (ARM is produced by default)
    -o      Enable all optimizations
    -oCEP   Enable constant evaluation and propagation
    -oCF    Enable control flow analysis
    -oIE    Enable instruction evaluation
```


## Authors

This project was completed from January to March 2022 together with Benson Zhou, Sipan Petrosyan & Vincent Lee