package de.kit.tva.cbc.eventsequence;

import java.util.ArrayList;
import java.util.List;

import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelFactory;
import de.tu_bs.cs.isf.cbc.cbcmodel.Event;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequence;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceCondition;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceConditions;
import de.tu_bs.cs.isf.cbc.util.CodeHandler;

/**
 * Parsing of strings to their appropriate event sequence data structures.
 * 
 * @author Fynn
 */
public class EventSequenceParser {
	public EventSequenceParser() {

	}

	/**
	 * Parse all of the event sequences contained in a {@link EventSequenceConditions} instance.
	 * 
	 * @param esc
	 * @return The parsed event sequences.
	 */
	public List<EventSequence> parse(EventSequenceConditions esc) {
		var eventSequences = new ArrayList<EventSequence>();
		for (var condition : esc.getEventSequenceConditions()) {
			eventSequences.addAll(parse(condition));
		}
		return eventSequences;
	}

	/**
	 * Parse all of the event sequences contained in a {@link EventSequenceCondition} instance.
	 * 
	 * @param condition
	 * @return The parsed event sequences.
	 */
	public List<EventSequence> parse(EventSequenceCondition condition) {
		return parseConditionStr(condition.getName());
	}

	// !eventSeq(event(c.m(p), x > 0), event...)
	private List<EventSequence> parseConditionStr(String condition) {
		var eventSequences = new ArrayList<EventSequence>();
		String helper = "";
		int startBracketIndex = -1;
		int endBracketIndex = -1;
		String curEventSeq = "";
		while (condition.indexOf(VerifyEventSequences.EVENT_SEQ_ID) != -1) {
			helper = condition.substring(0, condition.indexOf(VerifyEventSequences.EVENT_SEQ_ID));
			startBracketIndex = helper.length() + VerifyEventSequences.EVENT_SEQ_ID.length();
			endBracketIndex = CodeHandler.findClosingBracketIndex(condition, startBracketIndex, '(');
			curEventSeq = condition.substring(startBracketIndex - VerifyEventSequences.EVENT_SEQ_ID.length(),
					endBracketIndex);
			condition = condition.substring(endBracketIndex);
			var e = parseEventSeqStr(curEventSeq);
			eventSequences.add(e);
		}
		return eventSequences;
	}

	// eventSeq(event(c.m(p), x > 0), event...)
	private EventSequence parseEventSeqStr(String eventSeq) {
		EventSequence eSeq = CbcmodelFactory.eINSTANCE.createEventSequence();
		eSeq.setName(eventSeq);
		String helper = "";
		int startBracketIndex = -1;
		int endBracketIndex = -1;
		String curEventStr = "";
		while (eventSeq.indexOf(VerifyEventSequences.EVENT_CON_ID) != -1) {
			eventSeq.indexOf(VerifyEventSequences.EVENT_CON_ID);
			startBracketIndex = eventSeq.indexOf(VerifyEventSequences.EVENT_CON_ID)
					+ VerifyEventSequences.EVENT_CON_ID.length();
			endBracketIndex = CodeHandler.findClosingBracketIndex(eventSeq, startBracketIndex, '(');
			curEventStr = eventSeq.substring(startBracketIndex, endBracketIndex);
			Event curEvent = parseEventStr(curEventStr);
			eSeq.getEvents().add(curEvent);
		}
		return eSeq;
	}

	// event(c.m(p), x > 0)
	private Event parseEventStr(String event) {
		Event e = CbcmodelFactory.eINSTANCE.createEvent();
		var split = event.split("\\,");
		e.getCondition().setName(split[1]);
		var methodCall = split[0];
		e.setClassName(methodCall.split(".")[0]);
		var methodParams = methodCall.split(".")[1];
		e.setMethodName(methodParams.substring(0, methodParams.indexOf('(')));
		e.setParamTypes(methodParams.substring(methodParams.indexOf('(') + 1, methodParams.length() - 1));
		return e;
	}

	/**
	 * Parses the event sequences of a finished proof. We use a different event sequence syntax for KeY
	 * compared to CorC for readability reasons. See the event sequence docs for more information.
	 * 
	 * @param proofResult
	 * @return The parsed event sequences.
	 */
	public List<EventSequence> parse(String proofResult) {
		// TODO: Finish up the required method to make this work.
		var eventSequences = new ArrayList<EventSequence>();
		String helper = "";
		int startBracketIndex = -1;
		int endBracketIndex = -1;
		String curEventSeq = "";
		while (proofResult.indexOf(VerifyEventSequences.EVENT_SEQ_ID) != -1) {
			helper = proofResult.substring(0, proofResult.indexOf(VerifyEventSequences.EVENT_SEQ_ID));
			startBracketIndex = helper.length() + VerifyEventSequences.EVENT_SEQ_ID.length();
			endBracketIndex = CodeHandler.findClosingBracketIndex(proofResult, startBracketIndex, '(');
			curEventSeq = proofResult.substring(startBracketIndex - VerifyEventSequences.EVENT_SEQ_ID.length(),
					endBracketIndex);
			proofResult = proofResult.substring(endBracketIndex);
			EventSequence e;
		}
		return eventSequences;
	}

	private EventSequence parseKeYstrToEventSeq(String eventSeq) {
		var eSeq = CbcmodelFactory.eINSTANCE.createEventSequence();
		eSeq.setName(eventSeq);
		Event newEvent;
		return null;
	}

}
