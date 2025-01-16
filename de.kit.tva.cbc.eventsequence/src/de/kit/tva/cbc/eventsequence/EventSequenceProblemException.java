package de.kit.tva.cbc.eventsequence;

public class EventSequenceProblemException extends Exception {
	private static final long serialVersionUID = -2967605689930075556L;

	public EventSequenceProblemException(String msg) {
		super("EventSequenceProblemException: " + msg);
	}

}
