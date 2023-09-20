package de.tu_bs.cs.isf.cbc.mutation.feature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelFactory;
import de.tu_bs.cs.isf.cbc.cbcmodel.CompositionStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.Condition;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariable;
import de.tu_bs.cs.isf.cbc.cbcmodel.Rename;
import de.tu_bs.cs.isf.cbc.cbcmodel.Renaming;
import de.tu_bs.cs.isf.cbc.cbcmodel.SelectionStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.SmallRepetitionStatement;
import de.tu_bs.cs.isf.cbc.mutation.util.CodeRepresentationFinder;
import de.tu_bs.cs.isf.cbc.mutation.util.CopyDiagram;
import de.tu_bs.cs.isf.cbc.mutation.util.DirectoryCreator;
import de.tu_bs.cs.isf.cbc.mutation.util.JavaDirectoryLoader;
import de.tu_bs.cs.isf.cbc.tool.exceptions.CodeRepresentationFinderException;
import de.tu_bs.cs.isf.cbc.tool.helper.ClassHandler;
import de.tu_bs.cs.isf.cbc.tool.helper.CodeGenerator;
import de.tu_bs.cs.isf.cbc.tool.helper.CodeHandler;
import de.tu_bs.cs.isf.cbc.tool.helper.DiagramPartsExtractor;
import de.tu_bs.cs.isf.cbc.tool.helper.DiffChecker;
import de.tu_bs.cs.isf.cbc.tool.helper.FileHandler;
import de.tu_bs.cs.isf.cbc.tool.helper.IdAdder;
import de.tu_bs.cs.isf.cbc.tool.helper.IdRemover;
import de.tu_bs.cs.isf.cbc.tool.helper.LinePair;
import de.tu_bs.cs.isf.cbc.tool.helper.MethodHandler;
import de.tu_bs.cs.isf.cbc.tool.helper.UpdateDiagram;
import de.tu_bs.cs.isf.cbc.util.FileUtil;
import src.mujava.AllMutantsGenerator;
import src.mujava.MutationSystem;
import src.mujava.OpenJavaException;
import src.mujava.makeMuJavaStructure;

public class Mutator {
	public static String FOLDER_NAME = "mutations";

	private String className;
	private URI classUri;
	private String[] operators;
	private String originalCode;
	private String[] mutants; 
	private Diagram originalDiagram;
	private IFolder mutationFolder;
	private int mutationCount;
	
	public Mutator(List<String> operators) {
		this.operators = new String[operators.size()];
		this.mutationCount = 0;
		for (int i = 0; i < operators.size(); i++) {
			this.operators[i] = operators.get(i);
		}
	}
	
	public void mutate(Diagram diagramToMutate) throws Exception {
		originalDiagram = diagramToMutate;
		getClassInformation();
		originalCode = constructCode();
		setup();
		generateFiles();
		File[] mutants = getMutants();
		this.mutants = readCode(mutants);
		cleanUp();
	}

	public void generateDiagrams() throws CodeRepresentationFinderException {
		String originalCode = getOriginalCode();
		DiffChecker dc = new DiffChecker();
		for (String mutant : mutants) {
			dc.check(originalCode, mutant);
			LinePair diffLine = dc.nextDiff();	
			applyMutation(diffLine);
		}
	}
	
	private void setup() {
		mutationFolder = FileHandler.getInstance().getFolder(originalDiagram.eResource().getURI(), FOLDER_NAME);
		FileHandler.getInstance().deleteFolder(mutationFolder);
		FileHandler.getInstance().createFolder(originalDiagram.eResource().getURI(), FOLDER_NAME);
	}

	private void generateFiles() throws IOException, OpenJavaException {
		String location = setupMuJava();
		File targetFile = generateFile(location, originalDiagram.getName(), originalCode);
		executeMuJava(targetFile);
	}

	private String getOriginalCode() {
		if (originalCode == null) {
			return "";
		}
		return CodeHandler.indentCode(originalCode, 0);
	}
	
	private void getClassInformation() {
		String location = FileUtil.getProjectLocation(originalDiagram.eResource().getURI());
		className = originalDiagram.eResource().getURI().segment(originalDiagram.eResource().getURI().segmentCount() - 2);
		classUri = originalDiagram.eResource().getURI();//.trimSegments(1);
	}
	
	private String constructCode() throws Exception {
		ClassHandler ch = new ClassHandler(this.className, this.classUri);
		String diagramAsCode = CodeGenerator.getInstance().generateCodeFor(originalDiagram);
		String signature = MethodHandler.getSignatureFromCode(diagramAsCode);
		String contract = MethodHandler.getContractFromCode(diagramAsCode, signature);
		MethodHandler mh = new MethodHandler(contract, signature, diagramAsCode);
		ch.addMethod(mh);
		return ch.getCode();
	}
	
	private String setupMuJava() {
		String location = FileUtil.getProjectLocation(originalDiagram.eResource().getURI()) + File.separator + FOLDER_NAME;
		MutationSystem.CLASS_NAME = className;
		setMuJavaPaths(location);
		makeMuJavaStructure.main(null);
		return location;
	}
	
	private File generateFile(String location, String name, String code) throws IOException {
		location = location + File.separator + "src" + File.separator + name + ".java";
		File f = new File(location);
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		fw.write(code);
		fw.close();
		FileUtils.copyFile(f, new File(MutationSystem.CLASS_PATH + File.separator + f.getName()));
		return f;
	}
	
	private void setMuJavaPaths(String location) {
		MutationSystem.SYSTEM_HOME = location;
		MutationSystem.SRC_PATH = location + File.separator + "src";
		MutationSystem.CLASS_PATH = location + File.separator + "classes"; 
        MutationSystem.TESTSET_PATH = location + File.separator + "testset";
		MutationSystem.MUTANT_HOME = location + File.separator + "result";
		MutationSystem.MUTANT_PATH = MutationSystem.MUTANT_HOME;
		MutationSystem.TRADITIONAL_MUTANT_PATH = MutationSystem.MUTANT_PATH;
		MutationSystem.CLASS_MUTANT_PATH = MutationSystem.MUTANT_PATH;
		MutationSystem.EXCEPTION_MUTANT_PATH = MutationSystem.MUTANT_PATH;
		DirectoryCreator.makeDir(new File(location));
	}
	
	private void executeMuJava(File mutationTargetFile) throws OpenJavaException {
		String[] classNames = new String[] {""};
		AllMutantsGenerator allgen = new AllMutantsGenerator(mutationTargetFile, classNames, operators);
		allgen.makeMutants();
	}
	
	private File[] getMutants() {
		JavaDirectoryLoader loader = new JavaDirectoryLoader();
		File[] mutantFiles = loader.loadData(MutationSystem.MUTANT_HOME);
		return mutantFiles;
	}
	
	private String[] readCode(File[] mutants) throws FileNotFoundException {
		String[] codes = new String[mutants.length];
		int i = 0;
		for (File f : mutants) {
			codes[i] = readLines(f);
			i++;
		}
		return codes;
	}
	
	private String readLines(File f) throws FileNotFoundException {
		String code = "";
		Scanner scanner = new Scanner(f);
		code = clearScanner(scanner);
		code = CodeHandler.indentCode(code, 0);
		scanner.close();
		return code;
	}
	
	private String clearScanner(Scanner scanner) {
		String code = "";
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (isMuJavaComment(line)) {
				continue;
			}
			code += line + "\n";
		}
		return code;
	}
	
	private boolean isMuJavaComment(String line) {
		return line.contains("//");
	}
	
	private void cleanUp() throws CoreException {
		FileHandler.getInstance().deleteFolder(classUri, FOLDER_NAME);
		this.mutationFolder.refreshLocal(2, null);
	}
	
	private void applyMutation(LinePair diffLine) throws CodeRepresentationFinderException {
		this.mutationCount++;
		String mutantName = this.originalDiagram.getName() + "Mutant" + this.mutationCount;
		String diagCopyPath = this.mutationFolder.getLocation().toOSString() + File.separator + mutantName + ".diagram";
		CopyDiagram cd = new CopyDiagram(diagCopyPath, this.originalDiagram, mutantName);
		Diagram diagCopy = cd.get();
		changeTargetLine(diagCopy, diffLine);
		cd.save();
	}
	
	private void changeTargetLine(Diagram diagCopy, LinePair diffLine) throws CodeRepresentationFinderException {
		DiagramPartsExtractor dpe = new DiagramPartsExtractor(diagCopy);
		AbstractStatement firstStatement = dpe.getFormula().getStatement();
		CodeRepresentationFinder crf = new CodeRepresentationFinder();
		EObject target = crf.find(firstStatement, diffLine.originalLine);
		((AbstractStatement)target).setName(diffLine.newLine + "\n");
	}
	
}
