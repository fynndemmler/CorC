package de.tu_bs.cs.isf.cbc.tool.features;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;
import de.tu_bs.cs.isf.cbc.cbcmodel.GlobalConditions;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariables;
import de.tu_bs.cs.isf.cbc.cbcmodel.ReturnStatement;
import de.tu_bs.cs.isf.cbc.tool.exceptions.DiagnosticsException;
import de.tu_bs.cs.isf.cbc.tool.helper.Colors;
import de.tu_bs.cs.isf.cbc.tool.helper.Features;
import de.tu_bs.cs.isf.cbc.tool.helper.FileHandler;
import de.tu_bs.cs.isf.cbc.util.Console;
import diagnostics.DataCollector;
import diagnostics.DataType;

/**
 * Feature for testing all statements.
 * @author Fynn
 */
public class TestAllStatements extends MyAbstractAsynchronousCustomFeature{
	private final IFeatureProvider fp;
	
	public TestAllStatements(IFeatureProvider fp) {
		super(fp);
		this.fp = fp;
	}
	
	@Override
	public String getName() {
		return "Test all statements";
	}

	@Override
	public String getDescription() {
		return "Generates statement tests for the diagram.";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	@Override
	public void execute(ICustomContext context, IProgressMonitor monitor) {
		Console.clear();
		Console.println("Start testing...\n");
		final long startTime = System.nanoTime();
		
		testDiagram(getDiagram());
		
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		Console.println("Testing finished.");
		Console.println("Time needed: " + duration + "ms");	
	}
	
	public URI getUri(final Diagram diag) {
		final URI uri = diag.eResource().getURI();
		return uri;
	}
	
	public void testDiagram(final Diagram diag) {
		final URI uri;
		boolean returnStatement;
		
		JavaVariables vars = null;
		GlobalConditions conds = null;
		CbCFormula formula = null;
		for (Shape shape : diag.getChildren()) {
			Object obj = getBusinessObjectForPictogramElement(shape);
			if (obj instanceof JavaVariables) {
				vars = (JavaVariables) obj;
			} else if (obj instanceof GlobalConditions) {
				conds = (GlobalConditions) obj;
			} else if (obj instanceof CbCFormula) {
				formula = (CbCFormula) obj;
			}
		}	
		
		final TestStatement ts = new TestStatement(fp);
		uri = getUri(diag);
		DataCollector dataCollector;
		try {
			dataCollector = new DataCollector(uri, DataType.PATH, diag.getName());
		} catch (DiagnosticsException e) {
			e.printStackTrace();
			return;
		}
		ts.setUri(uri);
		FileHandler.getInstance().clearLog(diag.eResource().getURI());
		var allStatements = TestStatement.collectAllStatements(formula);
		Features features = null;
		if (FileHandler.getInstance().isSPL(uri)) {
			features = new Features(uri);
		} else {
			features = null;
		}	
		if (features == null) {
			for (var statement : allStatements) {
				returnStatement = statement instanceof ReturnStatement;
				float pathTime = ts.testPath((AbstractStatement)statement, vars, conds, formula, returnStatement, features);
				dataCollector.addData(ts.getStatementPath(statement), pathTime);
			}
			return;
		}
		Console.println("[SPL detected]", Colors.BLUE);
		for (int i = 0; i < features.getSize(); i++) {
			features.getNextConfig();
			Console.println(" > Configuration: [" + features.getConfigRep() + "]", Colors.BLUE);
			for (var statement : allStatements) {
				returnStatement = statement instanceof ReturnStatement;
				float pathTime = ts.testPath((AbstractStatement)statement, vars, conds, formula, returnStatement, features);
				dataCollector.addData(features.getCurConfigName(), ts.getStatementPath(statement), pathTime); 
			}
			FileHandler.getInstance().saveConfig(uri, formula, features, false);
		}
		dataCollector.finish();
	}

}
