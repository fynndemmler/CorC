package de.kit.tva.cbc.eventsequence;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;

public class EventSequenceContextUI extends AbstractCustomFeature implements EventSequenceProofObserver {
	public EventSequenceContextUI(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Event Sequences";
	}

	@Override
	public String getDescription() {
		return "View event sequence information for this refinement.";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes == null || pes.length != 1) {
			return false;
		}
		Object bo = getBusinessObjectForPictogramElement(pes[0]);
		if (bo == null) {
			return false;
		}
		if (!(bo instanceof AbstractStatement)) {
			return false;
		}
		AbstractStatement statement = (AbstractStatement) bo;
		if (statement.getRefinement() == null) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Information",
				"TODO");
	}

	@Override
	public void proofDone(EventSequenceProofInfo escProofInfo) {
		// TODO: display results so that they are accessible through the context button in the diagrams.
	}

}
