package de.tu_bs.cs.isf.cbc.tool.patterns;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Font;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.pattern.id.IdLayoutContext;
import org.eclipse.graphiti.pattern.id.IdPattern;
import org.eclipse.graphiti.pattern.id.IdUpdateContext;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;
import org.eclipse.graphiti.util.PredefinedColoredAreas;

import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelFactory;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceCondition;
import de.tu_bs.cs.isf.cbc.cbcmodel.EventSequenceConditions;
import de.tu_bs.cs.isf.cbc.util.CbcModelUtil;
import de.tu_bs.cs.isf.cbc.util.Console;

/**
 * Class that creates the graphical representation of event sequence conditions.
 * 
 * @author Fynn
 *
 */
public class EventSequenceConditionsPattern extends IdPattern implements IPattern {

	private static final String ID_NAME_TEXT = "globalConditionsName";
	private static final String ID_CONDITION_TEXT = "globalcondition";
	private static final String ID_MAIN_RECTANGLE = "mainRectangle";
	private static final String ID_HOR1_LINE = "hor1Line";

	public EventSequenceConditionsPattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return "Event Sequence Conditions";
	}

	@Override
	public String getCreateDescription() {
		return "Create a list of event sequence conditions.";
	}

	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return mainBusinessObject instanceof EventSequenceConditions;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		EventSequenceConditions conds = null;
		for (Shape shape : getDiagram().getChildren()) {
			Object obj = getBusinessObjectForPictogramElement(shape);
			if (obj instanceof EventSequenceConditions) {
				conds = (EventSequenceConditions) obj;
			}
		}
		if (conds != null)
			return false;
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		var eSeqCons = CbcmodelFactory.eINSTANCE.createEventSequenceConditions();
		/*
		var eSeqDummyCon = CbcmodelFactory.eINSTANCE.createEventSequenceCondition();
		eSeqDummyCon.setName("(Add new event sequence conditions)");
		eSeqCons.getEventsequencecondition().add(eSeqDummyCon);
		*/

		try {
			CbcModelUtil.saveToModelFile(eSeqCons, getDiagram());
		} catch (CoreException | IOException e) {
			Console.println(e.getMessage());
			e.printStackTrace();
		}

		addGraphicalRepresentation(context, eSeqCons);
		return new Object[] { eSeqCons };
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return super.canAdd(context) && context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public PictogramElement doAdd(IAddContext context) {

		Diagram targetDiagram = (Diagram) context.getTargetContainer();
		EventSequenceConditions addedEventSequenceConditions = (EventSequenceConditions) context.getNewObject();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();

		int width = context.getWidth() <= 0 ? 200 : context.getWidth();
		int height = context.getHeight() <= 0 ? 100 : context.getHeight();

		Font headerFont = gaService.manageFont(getDiagram(), "Arial", 9, false, true);

		// Main contents area
		ContainerShape outerContainerShape = peCreateService.createContainerShape(targetDiagram, true);
		RoundedRectangle mainRectangle = gaService.createRoundedRectangle(outerContainerShape, 20, 20);
		mainRectangle.setFilled(true);
		gaService.setRenderingStyle(mainRectangle, PredefinedColoredAreas.getBlueWhiteAdaptions());
		setId(mainRectangle, ID_MAIN_RECTANGLE);
		gaService.setLocationAndSize(mainRectangle, context.getX(), context.getY(), width, height);

		// create link and wire it
		link(outerContainerShape, addedEventSequenceConditions);

		// method name
		Shape nameTextShape = peCreateService.createShape(outerContainerShape, false);
		Text conditionsNameText = gaService.createText(nameTextShape, "Event Sequence Conditions");
		setId(conditionsNameText, ID_NAME_TEXT);
		conditionsNameText.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
		conditionsNameText.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
		conditionsNameText.setFont(headerFont);

		// line:
		Shape hor1Shape = peCreateService.createShape(outerContainerShape, false);
		Polyline hor1line = gaService.createPolyline(hor1Shape);
		setId(hor1line, ID_HOR1_LINE);

		link(outerContainerShape, addedEventSequenceConditions);
		link(nameTextShape, addedEventSequenceConditions);

		return outerContainerShape;
	}

	@Override
	protected boolean layout(IdLayoutContext context, String id) {
		boolean changesDone = false;

		GraphicsAlgorithm mainRectangle = context.getRootPictogramElement().getGraphicsAlgorithm();
		EventSequenceConditions eventSeqCons = (EventSequenceConditions) getBusinessObjectForPictogramElement(
				context.getRootPictogramElement());
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		int height = mainRectangle.getHeight();
		if (eventSeqCons.getEventsequencecondition().size() >= 1) {
			height = height / (eventSeqCons.getEventsequencecondition().size() + 1);
		}

		if (id.equals(ID_NAME_TEXT)) {
			Graphiti.getGaService().setLocationAndSize(ga, 0, 0, mainRectangle.getWidth(), height);
			changesDone = true;
		} else if (id.equals(ID_CONDITION_TEXT)) {
			int index = getIndex(context.getGraphicsAlgorithm());
			Graphiti.getGaService().setLocationAndSize(ga, 0, height * (index + 1), mainRectangle.getWidth(), height);
			changesDone = true;
		} else if (id.equals(ID_HOR1_LINE)) {
			Polyline polyline = (Polyline) ga;
			polyline.getPoints().clear();
			List<Point> pointList = Graphiti.getGaService()
					.createPointList(new int[] { 0, height, mainRectangle.getWidth(), height });
			polyline.getPoints().addAll(pointList);
			changesDone = true;
		}

		return changesDone;
	}

	@Override
	protected IReason updateNeeded(IdUpdateContext context, String id) {
		if (id.equals(ID_MAIN_RECTANGLE)) {
			ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
			var conditions = (EventSequenceConditions) context.getDomainObject();
			if (containerShape.getChildren().size() - 2 != conditions.getEventsequencecondition().size()) {
				return Reason.createTrueReason(
						"Number of Conditions differ. Expected: " + conditions.getEventsequencecondition().size() + " "
								+ (containerShape.getChildren().size() - 2));
			}
		}
		return Reason.createFalseReason();
	}

	@Override
	protected boolean update(IdUpdateContext context, String id) {
		if (id.equals(ID_MAIN_RECTANGLE)) {
			EList<EventSequenceCondition> conditions = ((EventSequenceConditions) context.getDomainObject())
					.getEventsequencecondition();
			while (((ContainerShape) context.getPictogramElement()).getChildren().size() - 2 < conditions.size()) {
				int newIndex = ((ContainerShape) context.getPictogramElement()).getChildren().size() - 2;
				var condition = conditions.get(newIndex);
				Shape shapeText = Graphiti.getPeCreateService()
						.createShape((ContainerShape) context.getPictogramElement(), true);
				MultiText conditionNameText = Graphiti.getGaService().createMultiText(shapeText, condition.getName());
				setId(conditionNameText, ID_CONDITION_TEXT);
				setIndex(conditionNameText, newIndex);
				conditionNameText.setHorizontalAlignment(Orientation.ALIGNMENT_CENTER);
				conditionNameText.setVerticalAlignment(Orientation.ALIGNMENT_CENTER);
				link(shapeText, condition);
			}
			return true;
		}
		return false;
	}
}
