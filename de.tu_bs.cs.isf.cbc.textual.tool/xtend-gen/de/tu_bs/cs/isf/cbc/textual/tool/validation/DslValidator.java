/**
 * generated by Xtext 2.10.0
 */
package de.tu_bs.cs.isf.cbc.textual.tool.validation;

import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.Condition;
import de.tu_bs.cs.isf.cbc.cbcmodel.MethodStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.ReturnStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.SelectionStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.SkipStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.SmallRepetitionStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.StrengthWeakStatement;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;

/**
 * This class contains custom validation rules.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 */
@SuppressWarnings("all")
public class DslValidator extends AbstractDslValidator {
  public static final String INVALID_NAME = "invalidName";
  
  public static final String NOT_PROVED = "notProved";
  
  @Check
  public void checkSyntaxOfStatement(final AbstractStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field CompareMethodBodies is undefined"
      + "\nreadAndTestMethodBodyWithJaMoPP2 cannot be resolved"
      + "\n! cannot be resolved");
  }
  
  @Check
  public void checkSyntaxOfRetunrStatement(final ReturnStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field CompareMethodBodies is undefined"
      + "\nreadAndTestMethodBodyWithJaMoPP2 cannot be resolved"
      + "\n! cannot be resolved");
  }
  
  @Check
  public void checkSyntaxOfCondition(final Condition condition) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method or field CompareMethodBodies is undefined"
      + "\nreadAndTestAssertWithJaMoPP cannot be resolved"
      + "\n! cannot be resolved");
  }
  
  @Check(CheckType.EXPENSIVE)
  public void checkProveOfStatement(final AbstractStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>");
  }
  
  @Check(CheckType.EXPENSIVE)
  public void checkProveOfReturnStatement(final ReturnStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>");
  }
  
  @Check(CheckType.EXPENSIVE)
  public void checkProveOfStrengthWeakStatement(final StrengthWeakStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>");
  }
  
  @Check(CheckType.EXPENSIVE)
  public void checkProveOfMethodStatement(final MethodStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>");
  }
  
  @Check(CheckType.EXPENSIVE)
  public void checkProveOfSkipStatement(final SkipStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>");
  }
  
  @Check(CheckType.EXPENSIVE)
  public void checkProveOfRepetitionStatement(final SmallRepetitionStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>");
  }
  
  @Check(CheckType.EXPENSIVE)
  public void checkProveOfSelectionStatement(final SelectionStatement statement) {
    throw new Error("Unresolved compilation problems:"
      + "\nThe method checkFileIsProven(URI, int) is undefined for the type Class<ProveWithKey>");
  }
}
