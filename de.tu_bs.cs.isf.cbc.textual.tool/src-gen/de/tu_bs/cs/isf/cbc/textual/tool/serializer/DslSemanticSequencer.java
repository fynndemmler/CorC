/*
 * generated by Xtext 2.10.0
 */
package de.tu_bs.cs.isf.cbc.textual.tool.serializer;

import com.google.inject.Inject;
import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCProblem;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelPackage;
import de.tu_bs.cs.isf.cbc.cbcmodel.CompositionStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.Condition;
import de.tu_bs.cs.isf.cbc.cbcmodel.GlobalConditions;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariable;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariables;
import de.tu_bs.cs.isf.cbc.cbcmodel.MethodStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.Rename;
import de.tu_bs.cs.isf.cbc.cbcmodel.Renaming;
import de.tu_bs.cs.isf.cbc.cbcmodel.ReturnStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.SelectionStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.SkipStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.SmallRepetitionStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.StrengthWeakStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.Variant;
import de.tu_bs.cs.isf.cbc.textual.tool.services.DslGrammarAccess;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.Action;
import org.eclipse.xtext.Parameter;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.serializer.ISerializationContext;
import org.eclipse.xtext.serializer.acceptor.SequenceFeeder;
import org.eclipse.xtext.serializer.sequencer.AbstractDelegatingSemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ITransientValueService.ValueTransient;

@SuppressWarnings("all")
public class DslSemanticSequencer extends AbstractDelegatingSemanticSequencer {

	@Inject
	private DslGrammarAccess grammarAccess;
	
	@Override
	public void sequence(ISerializationContext context, EObject semanticObject) {
		EPackage epackage = semanticObject.eClass().getEPackage();
		ParserRule rule = context.getParserRule();
		Action action = context.getAssignedAction();
		Set<Parameter> parameters = context.getEnabledBooleanParameters();
		if (epackage == CbcmodelPackage.eINSTANCE)
			switch (semanticObject.eClass().getClassifierID()) {
			case CbcmodelPackage.ABSTRACT_STATEMENT:
				sequence_AbstractStatement_Impl(context, (AbstractStatement) semanticObject); 
				return; 
			case CbcmodelPackage.CB_CFORMULA:
				sequence_CbCFormula(context, (CbCFormula) semanticObject); 
				return; 
			case CbcmodelPackage.CB_CPROBLEM:
				sequence_CbCProblem(context, (CbCProblem) semanticObject); 
				return; 
			case CbcmodelPackage.COMPOSITION_STATEMENT:
				sequence_CompositionStatement(context, (CompositionStatement) semanticObject); 
				return; 
			case CbcmodelPackage.CONDITION:
				sequence_Condition(context, (Condition) semanticObject); 
				return; 
			case CbcmodelPackage.GLOBAL_CONDITIONS:
				sequence_GlobalConditions(context, (GlobalConditions) semanticObject); 
				return; 
			case CbcmodelPackage.JAVA_VARIABLE:
				sequence_JavaVariable(context, (JavaVariable) semanticObject); 
				return; 
			case CbcmodelPackage.JAVA_VARIABLES:
				sequence_JavaVariables(context, (JavaVariables) semanticObject); 
				return; 
			case CbcmodelPackage.METHOD_STATEMENT:
				sequence_MethodStatement(context, (MethodStatement) semanticObject); 
				return; 
			case CbcmodelPackage.RENAME:
				sequence_Rename(context, (Rename) semanticObject); 
				return; 
			case CbcmodelPackage.RENAMING:
				sequence_Renaming(context, (Renaming) semanticObject); 
				return; 
			case CbcmodelPackage.RETURN_STATEMENT:
				sequence_ReturnStatement(context, (ReturnStatement) semanticObject); 
				return; 
			case CbcmodelPackage.SELECTION_STATEMENT:
				sequence_SelectionStatement(context, (SelectionStatement) semanticObject); 
				return; 
			case CbcmodelPackage.SKIP_STATEMENT:
				sequence_SkipStatement(context, (SkipStatement) semanticObject); 
				return; 
			case CbcmodelPackage.SMALL_REPETITION_STATEMENT:
				sequence_SmallRepetitionStatement(context, (SmallRepetitionStatement) semanticObject); 
				return; 
			case CbcmodelPackage.STRENGTH_WEAK_STATEMENT:
				sequence_StrengthWeakStatement(context, (StrengthWeakStatement) semanticObject); 
				return; 
			case CbcmodelPackage.VARIANT:
				sequence_Variant(context, (Variant) semanticObject); 
				return; 
			}
		if (errorAcceptor != null)
			errorAcceptor.accept(diagnosticProvider.createInvalidContextOrTypeDiagnostic(semanticObject, context));
	}
	
	/**
	 * Contexts:
	 *     AbstractStatement returns AbstractStatement
	 *     AbstractStatement_Impl returns AbstractStatement
	 *
	 * Constraint:
	 *     name=CodeString
	 */
	protected void sequence_AbstractStatement_Impl(ISerializationContext context, AbstractStatement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getAbstractStatement_ImplAccess().getNameCodeStringParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     CbCFormula returns CbCFormula
	 *
	 * Constraint:
	 *     (name=EString preCondition=Condition statement=AbstractStatement postCondition=Condition)
	 */
	protected void sequence_CbCFormula(ISerializationContext context, CbCFormula semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__NAME));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__PRE_CONDITION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__PRE_CONDITION));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__STATEMENT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__STATEMENT));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__POST_CONDITION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.CB_CFORMULA__POST_CONDITION));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getCbCFormulaAccess().getNameEStringParserRuleCall_1_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getCbCFormulaAccess().getPreConditionConditionParserRuleCall_4_0(), semanticObject.getPreCondition());
		feeder.accept(grammarAccess.getCbCFormulaAccess().getStatementAbstractStatementParserRuleCall_7_0(), semanticObject.getStatement());
		feeder.accept(grammarAccess.getCbCFormulaAccess().getPostConditionConditionParserRuleCall_11_0(), semanticObject.getPostCondition());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     CbCProblem returns CbCProblem
	 *
	 * Constraint:
	 *     (cbcformula=CbCFormula | globalcondition=GlobalConditions | javaVariable=JavaVariables | renaming=Renaming)+
	 */
	protected void sequence_CbCProblem(ISerializationContext context, CbCProblem semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AbstractStatement returns CompositionStatement
	 *     CompositionStatement returns CompositionStatement
	 *
	 * Constraint:
	 *     (firstStatement=AbstractStatement intermediateCondition=Condition secondStatement=AbstractStatement)
	 */
	protected void sequence_CompositionStatement(ISerializationContext context, CompositionStatement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.COMPOSITION_STATEMENT__FIRST_STATEMENT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.COMPOSITION_STATEMENT__FIRST_STATEMENT));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.COMPOSITION_STATEMENT__INTERMEDIATE_CONDITION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.COMPOSITION_STATEMENT__INTERMEDIATE_CONDITION));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.COMPOSITION_STATEMENT__SECOND_STATEMENT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.COMPOSITION_STATEMENT__SECOND_STATEMENT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getCompositionStatementAccess().getFirstStatementAbstractStatementParserRuleCall_2_0(), semanticObject.getFirstStatement());
		feeder.accept(grammarAccess.getCompositionStatementAccess().getIntermediateConditionConditionParserRuleCall_6_0(), semanticObject.getIntermediateCondition());
		feeder.accept(grammarAccess.getCompositionStatementAccess().getSecondStatementAbstractStatementParserRuleCall_9_0(), semanticObject.getSecondStatement());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Condition returns Condition
	 *
	 * Constraint:
	 *     name=EString
	 */
	protected void sequence_Condition(ISerializationContext context, Condition semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.CONDITION__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.CONDITION__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getConditionAccess().getNameEStringParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     GlobalConditions returns GlobalConditions
	 *
	 * Constraint:
	 *     (conditions+=Condition conditions+=Condition*)?
	 */
	protected void sequence_GlobalConditions(ISerializationContext context, GlobalConditions semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     JavaVariable returns JavaVariable
	 *
	 * Constraint:
	 *     name=EString
	 */
	protected void sequence_JavaVariable(ISerializationContext context, JavaVariable semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.JAVA_VARIABLE__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.JAVA_VARIABLE__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getJavaVariableAccess().getNameEStringParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     JavaVariables returns JavaVariables
	 *
	 * Constraint:
	 *     (variables+=JavaVariable variables+=JavaVariable*)?
	 */
	protected void sequence_JavaVariables(ISerializationContext context, JavaVariables semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AbstractStatement returns MethodStatement
	 *     MethodStatement returns MethodStatement
	 *
	 * Constraint:
	 *     name=EString
	 */
	protected void sequence_MethodStatement(ISerializationContext context, MethodStatement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getMethodStatementAccess().getNameEStringParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Rename returns Rename
	 *
	 * Constraint:
	 *     (type=EString function=EString newName=EString)
	 */
	protected void sequence_Rename(ISerializationContext context, Rename semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.RENAME__TYPE) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.RENAME__TYPE));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.RENAME__FUNCTION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.RENAME__FUNCTION));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.RENAME__NEW_NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.RENAME__NEW_NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getRenameAccess().getTypeEStringParserRuleCall_3_0(), semanticObject.getType());
		feeder.accept(grammarAccess.getRenameAccess().getFunctionEStringParserRuleCall_5_0(), semanticObject.getFunction());
		feeder.accept(grammarAccess.getRenameAccess().getNewNameEStringParserRuleCall_7_0(), semanticObject.getNewName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Renaming returns Renaming
	 *
	 * Constraint:
	 *     (rename+=Rename rename+=Rename*)?
	 */
	protected void sequence_Renaming(ISerializationContext context, Renaming semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AbstractStatement returns ReturnStatement
	 *     ReturnStatement returns ReturnStatement
	 *
	 * Constraint:
	 *     name=CodeString
	 */
	protected void sequence_ReturnStatement(ISerializationContext context, ReturnStatement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getReturnStatementAccess().getNameCodeStringParserRuleCall_2_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     AbstractStatement returns SelectionStatement
	 *     SelectionStatement returns SelectionStatement
	 *
	 * Constraint:
	 *     (guards+=Condition commands+=AbstractStatement (guards+=Condition commands+=AbstractStatement)*)
	 */
	protected void sequence_SelectionStatement(ISerializationContext context, SelectionStatement semanticObject) {
		genericSequencer.createSequence(context, semanticObject);
	}
	
	
	/**
	 * Contexts:
	 *     AbstractStatement returns SkipStatement
	 *     SkipStatement returns SkipStatement
	 *
	 * Constraint:
	 *     name=';'
	 */
	protected void sequence_SkipStatement(ISerializationContext context, SkipStatement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getSkipStatementAccess().getNameSemicolonKeyword_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     AbstractStatement returns SmallRepetitionStatement
	 *     SmallRepetitionStatement returns SmallRepetitionStatement
	 *
	 * Constraint:
	 *     (guard=Condition invariant=Condition variant=Variant loopStatement=AbstractStatement)
	 */
	protected void sequence_SmallRepetitionStatement(ISerializationContext context, SmallRepetitionStatement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__GUARD) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__GUARD));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__INVARIANT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__INVARIANT));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__VARIANT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__VARIANT));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__LOOP_STATEMENT) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.SMALL_REPETITION_STATEMENT__LOOP_STATEMENT));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getSmallRepetitionStatementAccess().getGuardConditionParserRuleCall_3_0(), semanticObject.getGuard());
		feeder.accept(grammarAccess.getSmallRepetitionStatementAccess().getInvariantConditionParserRuleCall_8_0(), semanticObject.getInvariant());
		feeder.accept(grammarAccess.getSmallRepetitionStatementAccess().getVariantVariantParserRuleCall_12_0(), semanticObject.getVariant());
		feeder.accept(grammarAccess.getSmallRepetitionStatementAccess().getLoopStatementAbstractStatementParserRuleCall_15_0(), semanticObject.getLoopStatement());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     AbstractStatement returns StrengthWeakStatement
	 *     StrengthWeakStatement returns StrengthWeakStatement
	 *
	 * Constraint:
	 *     (preCondition=Condition name=CodeString postCondition=Condition)
	 */
	protected void sequence_StrengthWeakStatement(ISerializationContext context, StrengthWeakStatement semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__PRE_CONDITION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__PRE_CONDITION));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__NAME));
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__POST_CONDITION) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.ABSTRACT_STATEMENT__POST_CONDITION));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getStrengthWeakStatementAccess().getPreConditionConditionParserRuleCall_3_0(), semanticObject.getPreCondition());
		feeder.accept(grammarAccess.getStrengthWeakStatementAccess().getNameCodeStringParserRuleCall_6_0(), semanticObject.getName());
		feeder.accept(grammarAccess.getStrengthWeakStatementAccess().getPostConditionConditionParserRuleCall_10_0(), semanticObject.getPostCondition());
		feeder.finish();
	}
	
	
	/**
	 * Contexts:
	 *     Variant returns Variant
	 *
	 * Constraint:
	 *     name=EString
	 */
	protected void sequence_Variant(ISerializationContext context, Variant semanticObject) {
		if (errorAcceptor != null) {
			if (transientValues.isValueTransient(semanticObject, CbcmodelPackage.Literals.VARIANT__NAME) == ValueTransient.YES)
				errorAcceptor.accept(diagnosticProvider.createFeatureValueMissing(semanticObject, CbcmodelPackage.Literals.VARIANT__NAME));
		}
		SequenceFeeder feeder = createSequencerFeeder(context, semanticObject);
		feeder.accept(grammarAccess.getVariantAccess().getNameEStringParserRuleCall_1_0(), semanticObject.getName());
		feeder.finish();
	}
	
	
}
