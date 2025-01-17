package de.kit.tva.cbc.eventsequence;

import java.util.regex.Pattern;

import de.tu_bs.cs.isf.cbc.util.Console;
import de.tu_bs.cs.isf.cbc.util.ProofInfo;
import de.tu_bs.cs.isf.cbc.util.ProofObserver;
import de.tu_bs.cs.isf.cbc.util.ProveWithKey;

/**
 * Responsible for verifying any event sequence conditions in a previously verified statement.
 * 
 * Note: This is not a separate feature since it is closely tied with the standard verification
 * process w.o. event sequences. Therefore, if event sequences are used, they need to be verified
 * along with the standard contracts.
 * 
 * @author Fynn
 */
public class VerifyEventSequences extends EventSequenceProofObservee implements ProofObserver {
	public static final String EVENT_SEQ_ID = Pattern.quote("eventSeq(");
	public static final String EVENT_CON_ID = Pattern.quote("event(");

	// TODO: The following comments, use lots of interfaces and many classes to
	// decouple more.
	// check whether event sequence conditions are present, if not, do nothing
	public VerifyEventSequences() {
		ProveWithKey.addObserver(this);
	}

	@Override
	public void proofDone(ProofInfo proofInfo) {
		if (proofInfo.getEventSequenceConditions() == null) {
			return;
		}
		this.escProofInfo = verifyEventSequenceConditions(proofInfo);
		if (this.escProofInfo == null) {
			return;
		}
		notifyObservers(); // Mainly useful for the UI context part of event sequences.

	}

	private EventSequenceProofInfo verifyEventSequenceConditions(ProofInfo proofInfo) {
		EventSequenceProofInfo espi = null;
		try {
			var esProblem = new EventSequenceProblem(proofInfo.getEventSequenceConditions(), proofInfo.getProblem());
			var newProofObligation = esProblem.getModifiedProblem();
			/* TODO: parse the results from KeY back into
			CorC (useful for parsing: We know that there are a // specific // number of
			logical ands that connect the conditions on the highest level, therefore
			regardless of // the reduction in the verification process, we simply split
			on these ands -> Exception: If a eventSequence is fully reduced, there
			might be LESS highest level ands.*/
			//...
			// TODO: store parsed results in a custom property of refinement rules 
			// ... 
		} catch (EventSequenceProblemException e) {
			e.printStackTrace();
			Console.println(e.getMessage());
			return null;
		}

		return espi;
	}

}
