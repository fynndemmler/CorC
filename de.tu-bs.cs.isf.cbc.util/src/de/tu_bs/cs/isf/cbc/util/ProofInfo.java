package de.tu_bs.cs.isf.cbc.util;

import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceConditions;

public class ProofInfo {
	private final EventSequenceConditions esc;
	private final CbCFormula formula;
	private final AbstractStatement verifiee;
	private final String problem;
	private final boolean isVerified;

	public ProofInfo(EventSequenceConditions esc, CbCFormula formula, AbstractStatement verifiee, String problem,
			boolean isVerified) {
		this.esc = esc;
		this.formula = formula;
		this.verifiee = verifiee;
		this.problem = problem;
		this.isVerified = isVerified;
	}

	/**
	 * @return the formula
	 */
	public CbCFormula getFormula() {
		return formula;
	}

	/**
	 * @return the verifiee
	 */
	public AbstractStatement getVerifiee() {
		return verifiee;
	}

	/**
	 * @return the problem
	 */
	public String getProblem() {
		return problem;
	}

	/**
	 * @return the isVerified
	 */
	public boolean isVerified() {
		return isVerified;
	}

	/**
	 * @return the esc
	 */
	public EventSequenceConditions getEventSequenceConditions() {
		return esc;
	}
}
