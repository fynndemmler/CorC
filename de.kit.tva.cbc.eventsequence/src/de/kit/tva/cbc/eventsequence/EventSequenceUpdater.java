package de.kit.tva.cbc.eventsequence;

import de.tu_bs.cs.isf.cbc.util.ProofInfo;
import de.tu_bs.cs.isf.cbc.util.ProofObserver;
import de.tu_bs.cs.isf.cbc.util.ProveWithKey;

/**
 * Every time a proof is finished, this class is responsible for updating the event sequences
 * property on the verifiee with the event sequences BEFORE the event sequence proof results are
 * parsed back into CorC. This is needed so that we can compare which event sequences where reduced.
 * 
 * @author Fynn
 */
public class EventSequenceUpdater implements ProofObserver {
	public EventSequenceUpdater() {
		ProveWithKey.addObserver(this);
	}

	@Override
	public void proofDone(ProofInfo proofInfo) {

	}

}
