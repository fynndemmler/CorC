package de.tu_bs.cs.isf.cbc.util;

import java.util.ArrayList;
import java.util.List;

public class ProofObservee {
	private static final List<ProofObserver> observers = new ArrayList<ProofObserver>();
	protected static ProofInfo proofInfo;

	public static void addObserver(ProofObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public static void removeObserver(ProofObserver observer) {
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	public static void notifyObservers() {
		for (var observer : observers) {
			observer.proofDone(proofInfo);
		}
	}
}
