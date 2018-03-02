/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.trolsoft.avrasmext.lexer;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author trol
 */
public class AvrxLanguageHierarchy extends LanguageHierarchy<AvrxTokenId> {

    @Override
    protected synchronized Collection<AvrxTokenId> createTokenIds() {
        return EnumSet.allOf(AvrxTokenId.class);
    }

    @Override
    protected synchronized Lexer<AvrxTokenId> createLexer(LexerRestartInfo<AvrxTokenId> info) {
        return new AvrxLexer(info);
    }

    @Override
    protected String mimeType() {
        return "text/x-avrx";
    }
}
