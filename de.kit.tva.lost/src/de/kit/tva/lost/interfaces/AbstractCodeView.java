package de.kit.tva.lost.interfaces;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import de.kit.tva.lost.models.parser.LostLexer;
import de.kit.tva.lost.models.parser.LostParser;
import de.kit.tva.lost.models.parser.LostParser.ProgramContext;

public class AbstractCodeView {
	protected ProgramContext parse(String code) {
		LostLexer lexer = new LostLexer(CharStreams.fromString(code));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LostParser parser = new LostParser(tokens);
		ProgramContext tree = parser.program();
		return tree;
	}
}
