/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.trolsoft.avrasmext.lexer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.LexerInput;
import ru.trolsoft.avrasmext.lexer.data.AvrInstructions;
import ru.trolsoft.avrasmext.lexer.data.AvrRegisters;
import ru.trolsoft.avrasmext.lexer.data.Keywords;


/**
 *
 * @author trol
 */
public class AvrxLexer implements Lexer<AvrxTokenId> {
	private final LexerRestartInfo<AvrxTokenId> info;

	private static final String OPERATORS = "+-=!,.<>;()[]{}|&*/^%:";

	private static final Logger log = Logger.getLogger(AvrxLexer.class.getName());


	private static enum State {
		DEFAULT,
		NEW_LINE,
		BLOCK_COMMENT,
		PREPROCESSOR,
	}

	private State state;

    AvrxLexer(LexerRestartInfo<AvrxTokenId> info) {
        this.info = info;
		  this.state = info.state() == null ? State.DEFAULT : State.values()[(Integer) info.state()];
    }

	@Override
	public Token<AvrxTokenId> nextToken() {
		LexerInput input = info.input();
		int ch = input.read();
		if (ch == LexerInput.EOF) {
			return null;
		} else if (isWhitespace(ch)) {
			if (ch == '\r' || ch == '\n') {
				if (state != State.BLOCK_COMMENT) {
					state = State.NEW_LINE;
				}
			}
			do {
				ch = input.read();
				if (ch == '\r' || ch == '\n') {
				if (state != State.BLOCK_COMMENT) {
					state = State.NEW_LINE;
				}
			}
			} while (isWhitespace(ch));
			input.backup(1);
			return create(AvrxTokenId.WHITESPACE);
		} else if (ch == ';') {
			do {
				ch = input.read();
			} while (ch != '\n' && ch != '\r' && ch != LexerInput.EOF);
			input.backup(1);
			return create(AvrxTokenId.COMMENT);
		} else if (ch == '.') {
			return checkAsmDirective(input);
		} else if (isOperator(ch)) {
			if (ch == '/') {
				ch = input.read();
				if (ch == '*') {
					state = State.BLOCK_COMMENT;
					return create(AvrxTokenId.COMMENT_BLOCK);
				} else if (ch == '/') {
					do {
						ch = input.read();
					} while (ch != '\n' && ch != '\r' && ch != LexerInput.EOF);
					input.backup(1);
					return create(AvrxTokenId.COMMENT);
				}
				input.backup(1);
			} else if (ch == '*' && state == State.BLOCK_COMMENT) {
				ch = input.read();
				if (ch == '/') {
					state = State.NEW_LINE;
					return create(AvrxTokenId.COMMENT_BLOCK);
				} else {
					input.backup(1);
				}
			} else if (ch == '<' && state == State.PREPROCESSOR) {
				int cnt = 0;
				do {
					ch = input.read();
					cnt++;
				} while (ch == '.' || ch == '/' || ch == '\\' || isLetter(ch) || isDigit(ch));
				if (ch == '>') {
					return create(AvrxTokenId.STRING);
				}
				input.backup(cnt);
			}
			return create(AvrxTokenId.OPERATOR);
		} else if (isDigit(ch)) {
			return checkNumber(input, ch);
		} else if (ch == '\'') {
			return checkCharacter(input);
		} else if (ch == '"') {
			return checkString(input);
		} else if (ch == '@') {
			return checkLocalLabel(input);
		} else if (ch == '#') {
			return checkPreprocessorDirective(input);
		} else if (ch == '$') {
				int cnt = 0;
				do {
					ch = input.read();
					cnt++;
				} while (isHexDigit(ch));
				if (cnt > 1) {
					input.backup(1);
					return create(AvrxTokenId.NUMBER_HEX);
				}
				input.backup(cnt);
				return create(AvrxTokenId.ERROR);
		} else if (isLetter(ch)) {
			do {
				ch = input.read();
			} while (isLetter(ch) || isDigit(ch));
			if (ch == ':') {
				 if (state == State.NEW_LINE) {
					return create(AvrxTokenId.LABEL);
				 } else {
					 return create(AvrxTokenId.ARGUMENT);
				 }
			}
			input.backup(1);

			 String str = input.readText().toString();
			 if (Keywords.isKeyword(str)) {
//				 log.info("keyword " + str);
				 return create(AvrxTokenId.AVRX_KEYWORD);
			 } else if (Keywords.isDirective(str)) {
//				 log.info("directive " + str);
				 return create(AvrxTokenId.ASM_DIRECTIVE);
			 } else if (AvrInstructions.isInstruction(str)) {
				 return create(AvrxTokenId.ASM_INSTRUCTION);
			 } else if (AvrRegisters.isRegister(str)) {
				 return create(AvrxTokenId.REGISTER);
			 } else if (AvrRegisters.isPair(str)) {
				 return create(AvrxTokenId.REGISTER_PAIR);
			 } else if (Keywords.isFlag(str)) {
				 return create(AvrxTokenId.FLAG);
			 } else if (Keywords.isArray(str)) {
				 return create(AvrxTokenId.ARRAY);
			 } else if (Keywords.isBuiltIn(str)) {
				 return create(AvrxTokenId.BUILTIN);
			 }
			 return create(AvrxTokenId.IDENTIFIER);
//			 log.info("??? " + str);
//		} else {
//			return create(AvrxTokenId.IDENTIFIER);
		}

		 return create(AvrxTokenId.ERROR);
    }

	private Token<AvrxTokenId> checkAsmDirective(LexerInput input) {
		int ch = input.read();
		if (isLetter(ch)) {
			do {
				ch = input.read();
			} while (isLetter(ch) || isDigit(ch));
			input.backup(1);
			String str = input.readText().toString();
			if (Keywords.isDirective(str)) {
				return create(AvrxTokenId.ASM_DIRECTIVE);
			}
			input.backup(str.length()-1);
			return create(AvrxTokenId.OPERATOR);
		}
		return create(AvrxTokenId.ERROR);
	}

	private Token<AvrxTokenId> checkCharacter(LexerInput input) {
		int next = input.read();
		int backupCount = 1;
		if (next == LexerInput.EOF) {
			return create(AvrxTokenId.ERROR);
		} else if (next == '\'') {
			next = input.read();
			backupCount++;
			if (next == LexerInput.EOF) {
				return create(AvrxTokenId.ERROR);
			}
		}
		next = input.read();
		backupCount++;
		switch (next) {
			case LexerInput.EOF:
				return create(AvrxTokenId.ERROR);
			case '\'':
				return create(AvrxTokenId.CHARACTER);
			default:
				input.backup(backupCount);
				return create(AvrxTokenId.ERROR);
		}
	}

	private Token<AvrxTokenId> checkString(LexerInput input) {
		int ch = 0;
		do {
			boolean slashed = ch == '\\';
			ch = input.read();
			if (slashed) {
				ch = ' ';
			}
		} while (ch != '\r' && ch != '\n' && ch != LexerInput.EOF && ch != '"');
		if (ch == '"') {
			return create(AvrxTokenId.STRING);
		} else {
			input.backup(1);
			return create(AvrxTokenId.ERROR);
		}
	}

	private Token<AvrxTokenId> checkNumber(LexerInput input, int ch) {
		if (ch == '0') {
			ch = input.read();
			int base;
			if (ch == 'x' || ch == 'X') {
				base = 16;
			} else if (ch == 'b' || ch == 'B') {
				base = 2;
			} else if (isDigit(ch)) {
				base = 8;
			} else if (isLetter(ch)) {
				return create(AvrxTokenId.ERROR);
			} else {
				input.backup(1);
				return create(AvrxTokenId.NUMBER);
			}
			do {
				ch = input.read();
			} while (isNumberChar(ch, base));
			input.backup(1);
			switch (base) {
				case 2:
					return create(AvrxTokenId.NUMBER_BIN);
				case 8:
					return create(AvrxTokenId.NUMBER_OCT);
				default:
					return create(AvrxTokenId.NUMBER_HEX);
			}
		}
		do {
				ch = input.read();
		} while (isDigit(ch));
		input.backup(1);
		return create(AvrxTokenId.NUMBER);
	}

	private Token<AvrxTokenId> checkLocalLabel(LexerInput input) {
		int ch;
		do {
			ch = input.read();
		} while (isLetter(ch) || isDigit(ch));
		if (ch == ':') {
			return create(AvrxTokenId.LABEL);
		} else {
			input.backup(1);
			return create(AvrxTokenId.LABEL);
		}
	}

	private Token<AvrxTokenId> checkPreprocessorDirective(LexerInput input) {
		if (state == State.BLOCK_COMMENT) {
			return create(AvrxTokenId.COMMENT_BLOCK);
		}
		int ch;
		do {
			ch = input.read();
		} while (isLetter(ch) || isDigit(ch));
		input.backup(1);
		String str = input.readText().toString();
		if (Keywords.isPreprocessor(str)) {
			state = State.PREPROCESSOR;
			return create(AvrxTokenId.PREPROCESSOR);
		}
		return create(AvrxTokenId.ERROR);
	}


	private static boolean isNumberChar(int ch, int base) {
		switch (base) {
			case 2:
				return ch == '0' || ch == '1';
			case 8:
				return ch >= '0' && ch <= '7';
			case 16:
				return isHexDigit(ch);
		}
		return false;
	}


	 private Token<AvrxTokenId> create(AvrxTokenId tokenId) {
		 if (state == State.BLOCK_COMMENT) {
			 return info.tokenFactory().createToken(AvrxTokenId.COMMENT_BLOCK);
		 } else if (tokenId != AvrxTokenId.WHITESPACE) {
			 if (state != State.PREPROCESSOR) {
				state = State.DEFAULT;
			 }
		 }
//if (tokenId == AvrxTokenId.ERROR) {
//	log.info("??? " + info.input().readText());
//}
		 return info.tokenFactory().createToken(tokenId);
	 }

    @Override
    public Object state() {
         return state == State.DEFAULT ? null : state.ordinal();
    }

    @Override
    public void release() {
    }

	private static boolean isOperator(int ch) {
		return OPERATORS.indexOf(ch) >= 0;
	}

	private static boolean isDigit(int ch) {
		return ch >= '0' && ch <= '9';
	}

	private static boolean isHexDigit(int ch) {
		return isDigit(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
	}

	private static boolean isLetter(int ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
	}

	private static boolean isWhitespace(int ch) {
		return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
	}


}
