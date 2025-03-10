package de.kit.tva.lost.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.swt.SWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.kit.tva.lost.models.codeviews.CodeModel;

public class CodeModelTests {
	private CodeModel instance = new CodeModel();

	@BeforeEach
	public void reset() {
		instance.setCode("");
	}

	@Test
	public void addEnter() {
		instance.addChar('\r');

		assertEquals("\n", instance.getCode());
	}

	@Test
	public void addTabbedEnterTwice() {
		instance.setCode("F\n" + "\tC\n" + "\t\treturn;");
		instance.setCurOffset(instance.getCode().length());

		instance.addChar('\r');

		assertEquals("F\n" + "\tC\n" + "\t\treturn;\n" + "\t\t", instance.getCode());
	}

	@Test
	public void removeChar() {
		instance.setCode("a");
		instance.setCurOffset(1);

		instance.addChar('\b');

		assertEquals("", instance.getCode());
	}

	@Test
	public void addIdentifierChar() {
		instance.addChar('b');

		assertEquals("b", instance.getCode());
	}

	@Test
	public void addBracket() {
		instance.addChar('(');

		assertEquals("(", instance.getCode());
	}

	@Test
	public void dontAddSpecialChar() {
		instance.addChar((char) SWT.SHIFT);

		assertEquals("", instance.getCode());
	}

	@Test
	public void normalBackspaceOffset() {
		instance.setCode("abc");
		instance.setCurOffset(instance.getCode().length());

		instance.addChar('\b');

		assertEquals(2, instance.getCurOffset());
	}

	@Test
	public void emptyBackSpaceOffset() {
		instance.addChar('\b');

		assertEquals(0, instance.getCurOffset());
	}

	@Test
	public void oneLetterBackSpaceOffset() {
		instance.setCode("a");

		instance.addChar('\b');

		assertEquals(0, instance.getCurOffset());
	}

}
