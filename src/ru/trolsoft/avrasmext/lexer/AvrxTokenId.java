/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.trolsoft.avrasmext.lexer;

import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.TokenId;

/**
 *
 * @author trol
 */
public enum AvrxTokenId implements TokenId {

    //KEYWORD ("keyword"), // NOI18N
    ASM_INSTRUCTION ("asm_instruction"), // NOI18N
	 ASM_DIRECTIVE ("asm_directive"), // NOI18N
	 PREPROCESSOR ("preprocessor"), // NOI18N
	 AVRX_KEYWORD("keyword"), // NOI18N
    OPERATOR ("operator"), // NOI18N
    LABEL ("label"), // NOI18N
	 ARGUMENT ("argument"), // NOI18N
    WHITESPACE ("whitespace"), // NOI18N
    NUMBER ("number"), // NOI18N
	 NUMBER_BIN ("number_bin"), // NOI18N
	 NUMBER_OCT ("number_oct"), // NOI18N
	 NUMBER_HEX ("number_hex"), // NOI18N
	 CHARACTER ("character"), // NOI18N
    STRING ("string"), // NOI18N
    IDENTIFIER ("identifier"), // NOI18N
    COMMENT ("comment"), // NOI18N
	 COMMENT_BLOCK ("comment_block"), // NOI18N
	 REGISTER ("register"), // NOI18N
	 REGISTER_PAIR ("pair"), // NOI18N
	 FLAG ("avrx_flag"), // NOI18N
	 ARRAY ("avrx_array"), // NOI18N
	 BUILTIN ("builtin"), // NOI18N
    ERROR ("error"); // NOI18N

	private final String name;

    AvrxTokenId(String name) {
        this.name = name;
    }

    @Override
    public String primaryCategory() {
        return name;
    }

	 public static Language<AvrxTokenId> getLanguage() {
	   return new AvrxLanguageHierarchy().language();
	}
}
