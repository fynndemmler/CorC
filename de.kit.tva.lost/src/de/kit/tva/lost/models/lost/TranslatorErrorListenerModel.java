package de.kit.tva.lost.models.lost;

import java.util.ArrayList;
import java.util.Date;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import de.kit.tva.lost.interfaces.CodeColor;
import de.kit.tva.lost.interfaces.Listener;
import de.kit.tva.lost.interfaces.Model;
import de.kit.tva.lost.interfaces.OperatorInfo;
import de.tu_bs.cs.isf.cbc.util.Colors;
import de.tu_bs.cs.isf.cbc.util.Console;

public class TranslatorErrorListenerModel extends BaseErrorListener implements Model {
	public static TranslatorErrorListenerModel getInstance() {
		if (instance == null) {
			instance = new TranslatorErrorListenerModel();
		}
		return instance;
	}

	public boolean errorOccurred() {
		return errorMsg != null;
	}

	public CodeColor getCodeColor() {
		return codeColor;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		errorMsg = new ErrorMessage();
		codeColor = new CodeColor();
		codeColor.colorToSet = Colors.RED;
		OperatorInfo info = new OperatorInfo(line - 1, charPositionInLine, charPositionInLine + 1);
		codeColor.info = info;
		errorMsg.line = line;
		errorMsg.charPosition = charPositionInLine;
		errorMsg.offender = offendingSymbol;
		errorMsg.text = msg;
		printMsg(errorMsg);
		notifyListeners();
		errorMsg = null;
	}

	@Override
	public void addListener(Listener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(Listener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	@Override
	public void notifyListeners() {
		listeners.forEach(l -> l.update());
	}

	private static TranslatorErrorListenerModel instance;

	private TranslatorErrorListenerModel() {
		this.listeners = new ArrayList<Listener>();
		this.errorMsg = null;
		this.codeColor = null;
	}

	private class ErrorMessage {
		int line;
		int charPosition;
		Object offender;
		String text;
	}

	private ArrayList<Listener> listeners;
	private ErrorMessage errorMsg;
	private CodeColor codeColor;

	private void printMsg(ErrorMessage msg) {
		var date = new Date(System.currentTimeMillis());
		try {
			Console.println(" [" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "]");
			Console.println(" > " + "Error during translation:", Colors.RED);
			Console.println("\t" + msg.text);
			Console.println("\tLine: " + msg.line + " at index " + msg.charPosition);
			Console.println("\tSymbol: " + msg.offender);
			Console.println("\n");
		} catch (NullPointerException e) {
			return;
		}
	}
}