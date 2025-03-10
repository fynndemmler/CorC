package de.tu_bs.cs.isf.cbc.tool.patterns;

import org.eclipse.core.resources.IProject;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.pattern.id.IdLayoutContext;
import org.eclipse.graphiti.pattern.id.IdPattern;
import org.eclipse.graphiti.pattern.id.IdUpdateContext;

import de.tu_bs.cs.isf.cbc.cbcclass.ModelClass;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelFactory;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariable;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariables;
import de.tu_bs.cs.isf.cbc.cbcmodel.VariableKind;
import de.tu_bs.cs.isf.cbc.parser.exceptions.IFbCException;
import de.tu_bs.cs.isf.cbc.tool.helper.GetProjectUtil;
import de.tu_bs.cs.isf.cbc.tool.helper.UpdateInformationFlow;
import de.tu_bs.cs.isf.lattice.Lattice;
import de.tu_bs.cs.isf.lattice.Lattices;

/**
 * Class that creates the graphical representation of Methods
 * 
 * @author Tobias
 *
 */
public class VariablePattern extends IdPattern implements IPattern {
	public static final String VARIABLE_KIND_PARAMETER = "param";
	public static final String VARIABLE_KIND_RETURN = "return";
	public static final String VARIABLE_KIND_GLOBAL = "global";
	public static final String VARIABLE_KIND_GLOBAL_PARAM = "global param";
	public static final String VARIABLE_KIND_RETURNPARAM = "returnparam";

	public static final String VARIABLE_KIND_LOCAL = "local";

	/**
	 * Constructor of the class
	 */
	public VariablePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return "Variable";
	}

	@Override
	public String getCreateDescription() {
		return "Create a variable.";
	}

	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return mainBusinessObject instanceof JavaVariable;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return getBusinessObjectForPictogramElement(context.getTargetContainer()) instanceof JavaVariables;
	}

	@Override
	public Object[] create(ICreateContext context) {
		JavaVariables variables = (JavaVariables) getBusinessObjectForPictogramElement(context.getTargetContainer());
		JavaVariable variable = CbcmodelFactory.eINSTANCE.createJavaVariable();
		variable.setKind(VariableKind.LOCAL);
		variable.setName("int a_" + variables.getVariables().size());
		variables.getVariables().add(variable);
		updatePictogramElement(context.getTargetContainer());
		return new Object[]{variable};
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return false;
	}

	@Override
	public PictogramElement doAdd(IAddContext context) {
		return null;
	}

	@Override
	protected boolean layout(IdLayoutContext context, String id) {
		return false;
	}

	public boolean canUpdate(IdUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof JavaVariable);
	}

	public IReason updateNeeded(IdUpdateContext context, String id) {
		Text nameText = (Text) context.getPictogramElement().getGraphicsAlgorithm();
		JavaVariable domainObject = (JavaVariable) getBusinessObjectForPictogramElement(context.getPictogramElement());
		if (domainObject.getName() == null || !nameText.getValue().equals(domainObject.getDisplayedName())) {
			return Reason.createTrueReason("Name differs. Expected: '" + domainObject.getName() + "'");
		}
		return Reason.createFalseReason();
	}

	public boolean update(IdUpdateContext context, String id) {
		Text nameText = (Text) context.getPictogramElement().getGraphicsAlgorithm();
		JavaVariable domainObject = (JavaVariable) getBusinessObjectForPictogramElement(context.getPictogramElement());
		nameText.setValue(domainObject.getDisplayedName());
		return true;
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		Object domainObject = getBusinessObjectForPictogramElement(context.getPictogramElement());
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		if (domainObject instanceof JavaVariable && ga instanceof Text) {
			if (((Text) ga).getValue().contains(VARIABLE_KIND_LOCAL.toUpperCase()))
				return true;
		}
		return false;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		JavaVariable variable = (JavaVariable) getBusinessObjectForPictogramElement(context.getPictogramElement());
		return variable.getDisplayedName();
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		if (value == null || value.length() == 0) {
			return "Variable must not be empty";
		}

		else if (value.length() > 0 && !value.toLowerCase()
				.matches("(" + VARIABLE_KIND_PARAMETER + "\\s" + "|" + VARIABLE_KIND_GLOBAL_PARAM + "\\s" + "|"
						+ VARIABLE_KIND_LOCAL + "\\s" + "|" + VARIABLE_KIND_RETURNPARAM + "\\s" + "|"
						+ VARIABLE_KIND_RETURN + "\\s" + "|" + VARIABLE_KIND_GLOBAL + "\\s" + "(static\\s)?"
						+ ")?(int|char|float|long|boolean|byte|short|double|([A-Za-z]\\w*))(\\[\\])?\\s[a-zA-Z]\\w*")) {
			return "Variable must contain a kind, a type and a name";
		}
		return null;
	}

	@Override
	public void setValue(String value, IDirectEditingContext context) {
		JavaVariable variable = (JavaVariable) getBusinessObjectForPictogramElement(context.getPictogramElement());
		if (value.trim().length() - value.trim().replaceAll(" ", "").length() == 1 /* || !value.contains("static") */) {
			variable.setKind(VariableKind.LOCAL);
			variable.setName(value);
		} else {
			if (value.toLowerCase().contains("global param")) {
				variable.setKind(VariableKind.GLOBAL_PARAM);
				value = value.toLowerCase();
				variable.setName(value.replaceFirst("global param ", ""));
			} else {
				variable.setKind(translateIntoVariableKind(value.substring(0, value.indexOf(" "))));
				variable.setName(value.substring(value.indexOf(" ") + 1));
			}

		}

		// Start of IFbC
		final IProject project = GetProjectUtil.getProjectForDiagram(getDiagram());
		final Lattice lattice = Lattices.getLatticeForProject(project);
		if (lattice != null) {
			for (Shape shape : getDiagram().getChildren()) {
				Object obj = getBusinessObjectForPictogramElement(shape);
				if (obj instanceof CbCFormula) {
					CbCFormula formula = (CbCFormula) obj;
					try {
						UpdateInformationFlow.updateInformationFlow(project.getName(), formula.getStatement(), lattice);
					} catch (IFbCException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		updatePictogramElement(((Shape) context.getPictogramElement()));
	}

	private VariableKind translateIntoVariableKind(String kindString) {
		VariableKind kind = VariableKind.LOCAL;
		kindString = kindString.toLowerCase();
		switch (kindString) {
			case VARIABLE_KIND_PARAMETER :
				kind = VariableKind.PARAM;
				break;
			case VARIABLE_KIND_RETURN :
				kind = VariableKind.RETURN;
				break;
			case VARIABLE_KIND_GLOBAL :
				kind = VariableKind.GLOBAL;
				break;
			// case VARIABLE_KIND_GLOBAL_PARAM:
			// kind = VariableKind.GLOBAL_PARAM;
			// break;
			case VARIABLE_KIND_RETURNPARAM :
				kind = VariableKind.RETURNPARAM;
				break;
		}
		return kind;
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		Shape shape = (Shape) context.getPictogramElement();
		Text text = (Text) shape.getGraphicsAlgorithm();
		JavaVariable jv = (JavaVariable) getBusinessObjectForPictogramElement(context.getPictogramElement());
		if (text.getValue().trim().endsWith("inherited")) {
			return false;
		}
		if (!(jv.eContainer() instanceof ModelClass) && (text.getValue().trim().startsWith("PUBLIC")
				|| text.getValue().trim().startsWith("RETURN") || text.getValue().trim().startsWith("PARAM"))) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canRemove(IRemoveContext context) {
		Shape shape = (Shape) context.getPictogramElement();
		Text text = (Text) shape.getGraphicsAlgorithm();
		JavaVariable jv = (JavaVariable) getBusinessObjectForPictogramElement(context.getPictogramElement());
		if (text.getValue().trim().endsWith("inherited")) {
			return false;
		}
		if (!(jv.eContainer() instanceof ModelClass) && (text.getValue().trim().startsWith("PUBLIC")
				|| text.getValue().trim().startsWith("RETURN") || text.getValue().trim().startsWith("PARAM"))) {
			return false;
		}
		return true;
	}

	@Override
	public void delete(IDeleteContext context) {
		Shape shape = (Shape) context.getPictogramElement();
		ContainerShape container = shape.getContainer();

		JavaVariable variable = (JavaVariable) getBusinessObjectForPictogramElement(context.getPictogramElement());
		if (variable.eContainer() != null && variable.eContainer() instanceof JavaVariables) {

			int indexToDelete = getIndex(shape.getGraphicsAlgorithm());

			for (Shape childShape : container.getChildren()) {
				if (getIndex(childShape.getGraphicsAlgorithm()) > indexToDelete) {
					setIndex(childShape.getGraphicsAlgorithm(), getIndex(childShape.getGraphicsAlgorithm()) - 1);
				}
			}
			super.delete(context);
			layoutPictogramElement(container);
		}
	}

}
