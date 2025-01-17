package de.kit.tva.cbc.eventsequence;

import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceConditions;

/**
 * Creates a new proof obligation for given event sequence conditions that simply replaces the
 * existing postcondition of a given proof obligation with a newly constructed event sequence
 * postcondition that concatenates all conditions using logical ands.
 * 
 * @author Fynn
 */
public class EventSequenceProblem {
	private String problem;

	public EventSequenceProblem(EventSequenceConditions esc, String standard_problem)
			throws EventSequenceProblemException {
		this.problem = standard_problem;
		var newPostCon = createEventSeqPostCon(esc);
		setPostCon(newPostCon);
	}

	public String getModifiedProblem() {
		return problem;
	}

	private String createEventSeqPostCon(EventSequenceConditions esc) {
		var escPostCon = new StringBuffer();
		for (var condition : esc.getEventSequenceConditions()) {
			escPostCon.append("(").append(condition).append(")").append(" & ");
		}
		if (esc.getEventSequenceConditions().size() > 0) {
			escPostCon.delete(escPostCon.length() - " & ".length(), escPostCon.length());
		}
		return escPostCon.toString();
	}

	private String setPostCon(String newPostCon) throws EventSequenceProblemException {
		StringBuffer modifiedProblem = new StringBuffer();
		var postConStart = this.problem.indexOf("\\>");
		if (postConStart == -1) {
			throw new EventSequenceProblemException("The proof problem doesn't contain a diamond modality.");
		}
		modifiedProblem.append(this.problem.substring(0, postConStart));
		modifiedProblem.append("(");
		modifiedProblem.append(newPostCon);
		modifiedProblem.append("\n}\n");
		return modifiedProblem.toString();
	}

}
