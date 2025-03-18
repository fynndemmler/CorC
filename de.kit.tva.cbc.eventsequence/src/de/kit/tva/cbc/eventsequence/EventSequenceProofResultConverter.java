package de.kit.tva.cbc.eventsequence;

import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelFactory;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceConditions;

/**
 * Given the contents of a .key file, this class converts the results of a proof in KeY back into a
 * {@link EventSequenceConditions}.
 * 
 * @author Fynn
 */
public class EventSequenceProofResultConverter {
	public EventSequenceProofResultConverter() {

	}

	/**
	 * Converter the results of a proof for event sequence conditions back into CorC's format.
	 *
	 * Assumes that event sequence conditions are concatenated using logical ands (& in JavaDL). Also
	 * assumes that every proof has only one statement and therefore a maximum of one event update. This
	 * allows for easy result parsing since we only need to know whether an event sequence was reduced
	 * or not.
	 * 
	 * @param proofResult The result of a proof containing only event sequence conditions.
	 * @return The newly constructed {@link EventSequenceConditions}.
	 */
	public EventSequenceConditions convert(String proofResult) {
		EventSequenceConditions newEsc = CbcmodelFactory.eINSTANCE.createEventSequenceConditions();

		return newEsc;
	}
}
