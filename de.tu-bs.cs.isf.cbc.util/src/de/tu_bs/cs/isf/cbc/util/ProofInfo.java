package de.tu_bs.cs.isf.cbc.util;

import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;

public class ProofInfo {
	private final CbCFormula formula;
	private final AbstractStatement verifiee;
	private final String problem;
	private final boolean isVerified;

	public ProofInfo(CbCFormula formula, AbstractStatement verifiee, String problem, boolean isVerified) {
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
}
