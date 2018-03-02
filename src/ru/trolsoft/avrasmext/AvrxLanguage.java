/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.trolsoft.avrasmext;

import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import ru.trolsoft.avrasmext.lexer.AvrxTokenId;

/**
 *
 * @author trol
 */
@LanguageRegistration(mimeType = "text/x-avrx")
public class AvrxLanguage extends DefaultLanguageConfig {

	@Override
	public Language getLexerLanguage() {
		return AvrxTokenId.getLanguage();
	}

	@Override
	public String getDisplayName() {
		return "AVRX";
	}

}
