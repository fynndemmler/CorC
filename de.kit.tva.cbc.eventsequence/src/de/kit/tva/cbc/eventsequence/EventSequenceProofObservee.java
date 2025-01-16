package de.kit.tva.cbc.eventsequence;

import java.util.ArrayList;
import java.util.List;

public class EventSequenceProofObservee {
	private final List<EventSequenceProofObserver> observers = new ArrayList<EventSequenceProofObserver>();
	protected EventSequenceProofInfo escProofInfo;

	public void addObserver(EventSequenceProofObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(EventSequenceProofObserver observer) {
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	public void notifyObservers() {
		for (var observer : observers) {
			observer.proofDone(escProofInfo);
		}
	}
}
