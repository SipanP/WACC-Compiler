# Sample Makefile for the WACC Compiler lab: edit this to build your own compiler

# Useful locations

ANTLR_DIR	 := antlr_config
SOURCE_DIR	 := src/main/kotlin
ANTLR_SOURCE_DIR := $(SOURCE_DIR)/antlr
FRONTEND_SOURCE_DIR := $(SOURCE_DIR)/frontend
OUTPUT_DIR	 := bin

# Project tools

ANTLR	:= antlrBuild
MKDIR	:= mkdir -p
JAVAC	:= javac
KOTLINC := kotlinc
RM	:= rm -rf

# Configure project Java flags

FLAGS   := -d $(OUTPUT_DIR) -cp bin:lib/antlr-4.9.3-complete.jar
JFLAGS	:= -sourcepath $(SOURCE_DIR) $(FLAGS)


# The make rules:

# run the antlr build script then attempts to compile all .java files within src/antlr
all:
	cd $(ANTLR_DIR) && ./$(ANTLR)
	mvn compile

# run all tests
test:
	mvn test

# run only the tests specified in the script
specific_tests:
	mvn test -Dtest="LexerTest, ParserTest, InvalidSyntaxTest, InvalidSemanticTest, ValidFileTest"

# clean up all of the compiled files
clean:
	mvn clean
	$(RM) *.s

.PHONY: all test specific_tests clean
