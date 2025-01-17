package de.tu_bs.cs.isf.cbc.tool.patterns;

import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.pattern.id.IdLayoutContext;
import org.eclipse.graphiti.pattern.id.IdPattern;
import org.eclipse.graphiti.pattern.id.IdUpdateContext;

import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelFactory;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceCondition;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceConditions;

/**
 * Class that creates the graphical representation of event sequence conditions.
 * 
 * @author Fynn
 *
 */
public class EventSequenceConditionPattern extends IdPattern implements IPattern {
	public EventSequenceConditionPattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return "Event Sequence Condition";
	}

	@Override
	public String getCreateDescription() {
		return "Create a event sequence condition. E.g. !eventSeq(eventCon(Class.method(param1), c > 0)";
	}

	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return mainBusinessObject instanceof EventSequenceCondition;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return getBusinessObjectForPictogramElement(context.getTargetContainer()) instanceof EventSequenceConditions;
	}

	@Override
	public Object[] create(ICreateContext context) {
		var condition = CbcmodelFactory.eINSTANCE.createEventSequenceCondition();
		condition.setName("?");
		EventSequenceConditions conditions = (EventSequenceConditions) getBusinessObjectForPictogramElement(
				context.getTargetContainer());
		conditions.getEventSequenceConditions().add(condition);
		updatePictogramElement(context.getTargetContainer());
		return new Object[] { condition };
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return super.canAdd(context) && context.getTargetContainer() instanceof EventSequenceConditions;
	}

	@Override
	public PictogramElement doAdd(IAddContext context) {
		return null;
	}

	@Override
	protected boolean layout(IdLayoutContext context, String id) {
		return false;
	}

	@Override
	protected IReason updateNeeded(IdUpdateContext context, String id) {
		if (context.getGraphicsAlgorithm() instanceof MultiText) {
			MultiText nameText = (MultiText) context.getGraphicsAlgorithm();
			var domainObject = (EventSequenceCondition) context.getDomainObject();
			if (domainObject.getName() == null || !(domainObject.getName().equals(nameText.getValue())
					|| nameText.getValue().equals("{" + domainObject.getName() + "}"))) {
				return Reason.createTrueReason("Name differs. Expected: '" + domainObject.getName() + "'");
			}
		}
		return Reason.createFalseReason();
	}

	@Override
	protected boolean update(IdUpdateContext context, String id) {
		if (context.getGraphicsAlgorithm() instanceof MultiText) {
			MultiText nameText = (MultiText) context.getGraphicsAlgorithm();
			var domainObject = (EventSequenceCondition) context.getDomainObject();
			nameText.setValue(domainObject.getName());
			return true;
		}
		return false;
	}

	@Override
	public int getEditingType() {
		return TYPE_MULTILINETEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		return true;
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return null;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context, String id) {
		var condition = (EventSequenceCondition) getBusinessObjectForPictogramElement(context.getPictogramElement());
		return condition.getName();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context, String id) {
		if (value == null) {
			return "Must not be empty";
		}
		return null;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context, String id) {
		var condition = (EventSequenceCondition) getBusinessObjectForPictogramElement(context.getPictogramElement());
		condition.setName(value.trim());
		// TODO: Call some update function in the event sequence handler instance, since
		// a event sequence condition has changed therefore old event sequence
		// information must be discarded/updated.
		updatePictogramElement(context.getPictogramElement());
	}

	@Override
	public boolean canRemove(IRemoveContext context) {
		return true;
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		return true;
	}

	@Override
	public void delete(IDeleteContext context) {
		Shape shape = (Shape) context.getPictogramElement();
		ContainerShape container = shape.getContainer();

		var condition = (EventSequenceCondition) getBusinessObjectForPictogramElement(context.getPictogramElement());
		if (condition.eContainer() != null && (condition.eContainer() instanceof EventSequenceConditions)) {
			int indexToDelete = getIndex(shape.getGraphicsAlgorithm());

			for (Shape childShape : container.getChildren()) {
				if (getIndex(childShape.getGraphicsAlgorithm()) > indexToDelete) {
					setIndex(childShape.getGraphicsAlgorithm(), getIndex(childShape.getGraphicsAlgorithm()) - 1);
				}
			}
			super.delete(context);
			layoutPictogramElement(container);
		}
		// TODO: Call some update function in the event sequence handler instance, since
		// a event sequence condition has changed therefore old event sequence
		// information must be discarded/updated.
	}
}
