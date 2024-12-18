package de.tu_bs.cs.isf.cbc.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import com.google.common.collect.Lists;

import com.google.common.hash.Hashing;

import de.tu_bs.cs.isf.cbc.cbcclass.CbcclassFactory;
import de.tu_bs.cs.isf.cbc.cbcclass.Field;
import de.tu_bs.cs.isf.cbc.cbcclass.Method;
import de.tu_bs.cs.isf.cbc.cbcclass.ModelClass;
import de.tu_bs.cs.isf.cbc.cbcclass.Parameter;
import de.tu_bs.cs.isf.cbc.cbcclass.Visibility;
import de.tu_bs.cs.isf.cbc.cbcmodel.AbstractStatement;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelFactory;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbcmodelPackage;
import de.tu_bs.cs.isf.cbc.cbcmodel.CompositionTechnique;
import de.tu_bs.cs.isf.cbc.cbcmodel.Condition;
import de.tu_bs.cs.isf.cbc.cbcmodel.GlobalConditions;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariable;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariables;
import de.tu_bs.cs.isf.cbc.cbcmodel.Renaming;
import de.tu_bs.cs.isf.cbc.cbcmodel.Variant;
import de.tu_bs.cs.isf.cbc.statistics.FileNameManager;
import de.tu_bs.cs.isf.cbc.util.consts.MetaNames;
import de.uka.ilkd.key.proof.Goal;
import de.uka.ilkd.key.proof.Proof;

public class ProveWithKey {
	public static final String SRC_FOLDER = ""; //Fix für Pfadfehler bei Repetition, Selection und Methodenaufrufen. War vorher "/src_gen"
	
	public static final String REGEX_ORIGINAL = "original";
	public static final String REGEX_RESULT = "\\\\result";

	public static final Pattern REGEX_THIS_KEYWORD = Pattern.compile("(?<![a-zA-Z0-9])(this)(?![a-zA-Z0-9])");
	public static final Pattern REGEX_RESULT_KEYWORD = Pattern.compile("(\\\\result)");
	public static final Pattern REGEX_OLD_KEYWORD = Pattern.compile("\\\\old\\((.*?)[\\)]+");
	public static final String OLD_VARS_SUFFIX = "_oldVal";
	public static HashMap<String, Integer> allStatistics = new HashMap<String, Integer>();

	AbstractStatement statement;
	private Diagram diagramToProve;
	private JavaVariables vars;
	private GlobalConditions conds;
	private Renaming renaming;
	private IProgressMonitor monitor;
	private String uri;
	private CbCFormula formula;
	private IFileUtil fileHandler;
	private String sourceFolder;
	private boolean isVariationalProject;
	private String configName;
	private String problem;
	private String subProofName = "";
	
	public ProveWithKey(AbstractStatement statement, Diagram diagram, IProgressMonitor monitor, IFileUtil fileHandler, String configName) {
		this(statement, diagram, monitor, fileHandler, ProveWithKey.SRC_FOLDER, configName);
	}

	public ProveWithKey(AbstractStatement statement, Diagram diagram, IProgressMonitor monitor, IFileUtil fileHandler, String srcFolder, String configName) {
		this.statement = statement;	
		this.monitor = monitor;
		this.fileHandler = fileHandler;
		this.diagramToProve = diagram;	
		DiagramPartsExtractor extractor = new DiagramPartsExtractor(diagramToProve);
    	vars = extractor.getVars();
		conds = extractor.getConds();
		renaming = extractor.getRenaming();
		formula = extractor.getFormula();		
		this.uri = diagramToProve.eResource().getURI().toPlatformString(true);

		if (uri.contains(MetaNames.FOLDER_NAME)) {
			String className = uri.substring(0, uri.lastIndexOf("/"));
			className = className.substring(className.lastIndexOf("/") + 1, className.length());
			this.sourceFolder = "/" + MetaNames.FOLDER_NAME; /*+ "/" + className;*/
		} else {
			this.sourceFolder = srcFolder;
		}
		this.isVariationalProject = false;
		this.configName = configName.equals("") ? "" : ("/" + configName);
		IProject project = FileUtil.getProjectFromFileInProject(URI.createURI(uri));
		try {
			if (project.getNature("de.ovgu.featureide.core.featureProjectNature") != null) {
				this.isVariationalProject = true;
			} else {
				this.isVariationalProject = false;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public boolean proveStatementWithKey(boolean returnStatement, boolean inlining, String callingClass, boolean forceProving) {
		File location = createProveStatementWithKey(null, null, null, true, "", "", returnStatement, callingClass);
		
		// TODO: proving will only skip if already true
		if (!forceProving) {
			String problemHash = Hashing.sha256().hashString(problem, StandardCharsets.UTF_8).toString();
			String filePath =location.getAbsolutePath().toString();
			String proveFolderLocation = filePath.substring(0, filePath.lastIndexOf(File.separator));
			FileNameManager fileManager = new FileNameManager();
			if (fileManager.isKeYFileWithHashProven(problemHash, proveFolderLocation) && !isVariationalProject) {
				Console.println("  Already true... skip\n");
				return true;
			}
		}
		
		Console.println("  Verify Pre -> {Statement} Post");
		return proveWithKey(location, inlining);
	}

	public boolean proveStatementWithKey(List<CbCFormula> refinementsOriginal, List<CbCFormula> refinements, List<JavaVariables> refinementsVars, boolean returnStatement, boolean inlining, String callingMethod, String varM, String callingClass, boolean forceProving) {
		File location = createProveStatementWithKey(refinementsOriginal, refinements, refinementsVars, true, callingMethod, varM, returnStatement, callingClass);
		if (!forceProving) {
			String problemHash = Hashing.sha256().hashString(problem, StandardCharsets.UTF_8).toString();
			String filePath = location.getAbsolutePath().toString();
			String proveFolderLocation = filePath.substring(0, filePath.lastIndexOf(File.separator));
			FileNameManager fileManager = new FileNameManager();
			if (fileManager.isKeYFileWithHashProven(problemHash, proveFolderLocation) && !isVariationalProject) {
				Console.println("  Already true... skip\n");
				return true;
			}
		}
		Console.println("  Verify Pre -> {Statement} Post");
		return proveWithKey(location, inlining);
	}
	
	private Collection<Parameter> copyMethodParams(String uri, String callingMethod, String callingClass) {
		var project = FileUtil.getProjectLocationS(uri);
		ModelClass clazz = FileHandler.instance.getClassOf(this.diagramToProve);
		Method method = clazz.getMethods().stream().filter(m -> m.getName().equals(callingMethod)).findFirst().get();
		return EcoreUtil.copyAll(method.getParameters());
	}

	public File createProveStatementWithKey(List<CbCFormula> refinementsOriginal, List<CbCFormula> refinements, List<JavaVariables> refinementsVars, boolean override, String callingMethod, String varM, boolean returnStatement, String callingClass) {
		String callingFeature = FeatureUtil.getInstance().getCallingFeature(uri);
		KeYFileContent content = new KeYFileContent(fileHandler.getProjectLocation(uri));
		JavaVariables varsFromJavaClass = readFieldsFromClass(callingClass);
		addExistingVarsTo(varsFromJavaClass);
		content.setSrcFolder(sourceFolder);
		content.readVariables(varsFromJavaClass);
		JavaVariable returnVariable = content.readVariables(vars);
		
		content.readGlobalConditions(conds);
		readPrePostModVars(varM.length() == 0 ? refinements : refinementsOriginal, refinementsVars, returnVariable, content, callingClass);
		
		if (returnStatement) {
			content.setStatement(";");
			content.handleReturn(statement, returnVariable, formula);
		} else {
			content.setStatement(statement.getName());	
		}
		content.rename(renaming);
		replaceOriginalInStatement(refinementsOriginal, refinements, refinementsVars, callingMethod, content, varM, callingClass, callingFeature);
		content.addSelf(formula);
		
		problem = content.getKeYStatementContent();	
		problem = problem.replaceAll("static", "");
		problem = problem.replaceAll("return", ""); // TODO replace with correct handling of return
		
		String location = fileHandler.getLocationString(uri) + configName;
		File keyFile = fileHandler.writeFile(problem, location, override, statement, subProofName);
		return keyFile;
	}

	private JavaVariables readFieldsFromClass(String className) {
		JavaVariables vars = CbcmodelFactory.eINSTANCE.createJavaVariables();
		fileHandler = new FileUtil(uri);
		File file = fileHandler.getClassFile(className);
		if (file == null) {
			return vars;
		}
		List<String> lines = fileHandler.readFileInList(file.getAbsolutePath());
		if (lines.get(0).contains(" extends ")) {
			String inheritedClassName = lines.get(0).trim();
			inheritedClassName = inheritedClassName.substring(inheritedClassName.indexOf(" extends ") + 9, inheritedClassName.indexOf("{")).trim();
			vars = readFieldsFromClass(inheritedClassName);
		}
		int i = 2;
		while (lines.get(i).contains(";")) {
			String field = lines.get(i++).replace(";", "");
			Field f = CbcclassFactory.eINSTANCE.createField();
			String split[] = field.replace("/*@spec_public@*/ ","").trim().split(" ");
			int pointer = 1;
			switch (split[0].toLowerCase()) {
			case "private":
				f.setVisibility(Visibility.PRIVATE);
				break;
			case "public":
				f.setVisibility(Visibility.PUBLIC);
				break;
			case "protected":
				f.setVisibility(Visibility.PROTECTED);
				break;
			case "package":
				f.setVisibility(Visibility.PACKAGE);
				break;
			default:
				f.setVisibility(Visibility.PUBLIC);
				pointer = 0;
				break;
			}
			if (Arrays.stream(split).anyMatch("static"::equalsIgnoreCase)) {
				f.setIsStatic(true);
				pointer++;
			} else
				f.setIsStatic(false);
			if (Arrays.stream(split).anyMatch("final"::equalsIgnoreCase)) {
				f.setIsFinal(true);
				pointer++;
			} else
				f.setIsFinal(false);

			f.setType(split[pointer++]);
			f.setName(split[pointer]);
			if (!f.getType().trim().equals("")) {
				vars.getFields().add(f);
			}
		}
		return vars;
	}

	private List<Condition> readInvariantsFromClass(String className) {
		List<Condition> invariants = new ArrayList<>();
		fileHandler = new FileUtil(uri);
		File file = fileHandler.getClassFile(className);
		if (file == null) {
			return invariants;
		}
		List<String> lines = fileHandler.readFileInList(file.getAbsolutePath());
		for (String line : lines) {
			if (line.contains("/*@ invariant")) {
				String conditionName = line.trim().replace("/*@ invariant ", "").replace("; @*/", "");
				Condition c = CbcmodelFactory.eINSTANCE.createCondition();
				c.setName(conditionName);
				invariants.add(c);
			}
		}
		return invariants;
	}
	
	public void readPrePostModVars(List<CbCFormula> refinements, List<JavaVariables> refinementsVars, JavaVariable returnVariable, KeYFileContent content, String callingClass) {
		CbCFormula formula = getCbCFormula(statement);
		String preFormula = Parser.getConditionFromCondition(formula.getStatement().getPreCondition().getName());
		String postFormula = Parser.getConditionFromCondition(formula.getStatement().getPostCondition().getName());
		String pre = Parser.getConditionFromCondition(statement.getPreCondition().getName());
		String post = Parser.getConditionFromCondition(statement.getPostCondition().getName());
		List<String> modifiables = statement.getPostCondition().getModifiables();
		
		String preInherited = applyLiskovInheritance(pre, preFormula, "pre");
		String postInherited = applyLiskovInheritance(post, postFormula, "post");
		String preInvariant = "";
		String postInvariant = "";
		
		List<Condition> invariants = readInvariantsFromClass(callingClass);
		for (Condition c : invariants) {
			preInvariant += " & " + c.getName();
			postInvariant += " & " + c.getName();
		}
		
		if (refinements != null && pre.equals(preFormula)) { // TODO composition only for pre post of formula. Extend to every case
			pre = composeContractForCbCDiagram(formula.getCompositionTechnique(), refinements, pre,	Parser.KEYWORD_JML_PRE, returnVariable);
		}
		if (refinements != null && post.equals(postFormula)) {
			modifiables = composeModifiables(refinements, refinementsVars, modifiables, formula.getCompositionTechnique(), true);
			post = composeContractForCbCDiagram(formula.getCompositionTechnique(), refinements, post, Parser.KEYWORD_JML_POST, returnVariable);
		}

		pre = "(" + pre + preInherited + ")";
		post = "(" + post + postInherited + ")";
		
		if (pre.equals(preFormula)) pre += preInvariant;
		if (pre.equals(preFormula)) post += postInvariant;
		
		content.setPreFromCondition(resolveResultKeyword(pre, returnVariable));
		content.setPostFromCondition(resolveResultKeyword(post, returnVariable));
		
		List<String> unmodifiedVariables = Parser.getUnmodifiedVars(modifiables, vars);
		unmodifiedVariables = unmodifiedVariables.stream().distinct().collect(Collectors.toList());
		content.addUnmodifiableVars(unmodifiedVariables);

		if (pre == null || pre.length() == 0) {
			content.setPreFromCondition("true");
		}
		if (post == null || post.length() == 0) {
			content.setPostFromCondition("true");
		}
	}

	private String applyLiskovInheritance(String cond, String condFormula, String type) {
		if (formula.getMethodObj() != null && formula.getMethodObj().getParentClass().getInheritsFrom() != null) {
			if (cond.equals(condFormula)) {
				for (Method m : formula.getMethodObj().getParentClass().getInheritsFrom().getMethods()) {
					if (m.getName().equals(formula.getMethodObj().getName())) {
						switch (type) {
							case "pre" :
								return " | (" + Parser.getConditionFromCondition(m.getCbcStartTriple().getStatement().getPreCondition().getName()) + ")";
							case "post" :
								return " & (" + Parser.getConditionFromCondition(m.getCbcStartTriple().getStatement().getPostCondition().getName()) + ")";
						}
					}
				}
			}
		}
		return "";
	}

	public void replaceOriginalInStatement(List<CbCFormula> refinementsOriginal, List<CbCFormula> refinements, List<JavaVariables> refinementsVars, String callingMethod, KeYFileContent content,
			String varM, String callingClass, String callingFeature) {
		if (refinements != null && refinements.size() > 0 && content.getStatement().contains("(")) {
			generateComposedClass(refinementsOriginal, refinements, refinementsVars, callingMethod, varM, callingClass, callingFeature);
			if (content.getStatement().contains("this.original")) {
				content.setStatement(content.getStatement().replaceFirst("this.original", "self.original_" + callingMethod));
			} else content.setStatement(content.getStatement().replaceFirst("original", "self" + ".original_" + callingMethod));
			
			if (!varM.equals("")) {
				if (content.getStatement().contains(callingClass + "." + varM)) {
					content.setStatement(content.getStatement().replaceFirst(callingClass + "." + varM.split("\\.")[1].toLowerCase(),	"self" + "." + varM.split("\\.")[1].toLowerCase()));
				} else if (!content.getStatement().contains("." + varM.split("\\.")[1].toLowerCase())) {
					content.setStatement(content.getStatement().replaceFirst(varM.split("\\.")[1].toLowerCase(), "self" + "." + varM.split("\\.")[1].toLowerCase()));
				}
			}
		}
	}

	List<String> composeModifiables(List<CbCFormula> refinements, List<JavaVariables> refinementsVars, List<String> modifiables,	CompositionTechnique compTechnique, boolean includeFormulaModifiable) {
		if (refinements != null && refinements.size() > 0) {
			for (int i = 0; i < refinements.size(); i++) {
				if (!includeFormulaModifiable) {
					compTechnique = refinements.get(i).getCompositionTechnique();
					String modifiableOriginal = Parser.getModifieableVarsFromConditionExceptLocals(refinements.get(i).getStatement().getPostCondition(), null, refinementsVars.get(i), null);
					List<String> modifiableList = new ArrayList<String>();
					if (!modifiableOriginal.equals("")) {
						modifiableList = Parser.getModifiedVarsFromCondition("modifiable(" + modifiableOriginal + ");");
					}
					modifiables = applyCompositionTechniqueOnModifiables(modifiableList, modifiableOriginal, compTechnique);
					includeFormulaModifiable = true;
				} else {
					if (i < refinementsVars.size()) {
					String modifiableOriginal = Parser.getModifieableVarsFromConditionExceptLocals(refinements.get(i).getStatement().getPostCondition(), null, refinementsVars.get(i), null);
					modifiables = applyCompositionTechniqueOnModifiables(modifiables, modifiableOriginal, compTechnique);
					}
				}
				if (compTechnique == CompositionTechnique.CONTRACT_OVERRIDING || (compTechnique == CompositionTechnique.EXPLICIT_CONTRACTING && !modifiables.contains(REGEX_ORIGINAL))) {
					return modifiables;
				}
			}
		}
		return modifiables;
	}

	private static List<String> applyCompositionTechniqueOnModifiables(List<String> modifiables, String modifiableOriginal, CompositionTechnique compTechnique) {
		switch (compTechnique) {
		case CONTRACT_OVERRIDING:
			break;
		case EXPLICIT_CONTRACTING:
			modifiables.remove(REGEX_ORIGINAL);
			for (String var : modifiableOriginal.split(",")) {
				if (var != "" && !modifiables.contains(var)) {
					modifiables.add(var);
				}
			}
			break;
		case CONJUNCTIVE_CONTRACTING:
			modifiables.addAll(Lists.newArrayList(modifiableOriginal.split(",")));
			break;
		}
		return modifiables;
	}

	private static String applyCompositionTechnique(String condition, String conditionOriginal,	CompositionTechnique compositionTechnique) {
		String composedCondition = condition;
		switch (compositionTechnique) {
		case CONTRACT_OVERRIDING:
			composedCondition = condition;
			break;
		case CONJUNCTIVE_CONTRACTING:
			if (conditionOriginal != "") {
				composedCondition = "(" + condition + ") & (" + conditionOriginal + ")";
			} else {
				composedCondition = condition;
			}
			break;
		case EXPLICIT_CONTRACTING:
			Pattern pattern = Pattern.compile(REGEX_ORIGINAL);
			Matcher matcher = pattern.matcher(condition);
			composedCondition = matcher.replaceAll(Matcher.quoteReplacement(conditionOriginal));
		}
		return composedCondition;
	}

	private String composeContractForCbCDiagram(CompositionTechnique compositionTechnique, List<CbCFormula> refinements, String condition, String keyword, JavaVariable returnVariable) {
		String composedCondition = condition;
		CompositionTechnique compTechnique = compositionTechnique;
		for (int i = 0; i < refinements.size(); i++) {
			if (i != 0) {
				refinements.get(i - 1).getCompositionTechnique();
			}
			String conditionOriginal = "";
			switch (keyword) {
			case "requires":
				conditionOriginal = Parser.getConditionFromCondition(refinements.get(i).getStatement().getPreCondition().getName()).replace("\n", "").replace("\r", "");
				break;
			case "ensures":
				conditionOriginal = Parser.getConditionFromCondition(refinements.get(i).getStatement().getPostCondition().getName()).replace("\n", "").replace("\r", "");
				break;
			}
			composedCondition = applyCompositionTechnique(composedCondition, conditionOriginal, compTechnique);
			if (compTechnique == CompositionTechnique.CONTRACT_OVERRIDING || (compTechnique == CompositionTechnique.EXPLICIT_CONTRACTING && !composedCondition.contains(REGEX_ORIGINAL))) {
				return resolveResultKeyword(composedCondition, returnVariable);
			}
		}
		return resolveResultKeyword(composedCondition, returnVariable);
	}

	public void generateComposedClass(List<CbCFormula> refinementsOriginal, List<CbCFormula> refinements, List<JavaVariables> refinementsVars, String callingMethod, String varM, String callingClass, String callingFeature) {
		boolean originalNecessary = refinementsOriginal != null;
		int round = 0;
		String className = varM.equals("") ? callingClass : varM.split("\\.")[0];
		String methodName = varM.equals("") ? ("original_" + callingMethod) : varM.split("\\.")[1].toLowerCase();
		while (originalNecessary || round == 0) {
			File file = fileHandler.getClassFile(className);
			String methodStub = Parser.getMethodStubFromFile(className, methodName, fileHandler).replaceAll("\n", "");

			String methodPreCondition = composeContractForCalledOriginal(refinements, Parser.KEYWORD_JML_PRE);
			String methodPostCondition = composeContractForCalledOriginal(refinements, Parser.KEYWORD_JML_POST);
			// adding of class invs is necessary as .key requires invs to be fulfilled, but called method(stub) does not
			if (refinements != null && refinements.size() > 0) {
				for (Condition c : readInvariantsFromClass(refinements.get(0).getClassName())) {
					methodPostCondition = methodPostCondition + " && " + Parser.rewriteConditionToJML(c.getName());
				}
			}
			
			List<String> assignables = composeModifiables(refinements, refinementsVars, new ArrayList<String>(), null, false);
			String assignableString = "";
			if (!assignables.isEmpty()) {
				assignableString = "@ assignable " + String.join(",", assignables) + ";";
			}
			if (vars != null) {
				for (JavaVariable actVar : vars.getVariables()) {
					if ((actVar.getKind().getName() != "PARAM")) {
						String splitName[] = actVar.getName().split(" ");
						assignableString = assignableString.replaceAll("," + splitName[splitName.length - 1], "");
						assignableString = assignableString.replaceAll(splitName[splitName.length - 1] + ";", ";");
						assignableString = assignableString.replaceAll(splitName[splitName.length - 1] + ",", "");
					}
				}
			}
			if (assignableString.trim().equals("@ assignable ;")) {
				assignableString = "@ assignable \\nothing;";
			}
			assignableString = assignableString.replaceAll("\\)", "").replaceAll("\\(", "");

			List<String> lines = fileHandler.readFileInList(file.getAbsolutePath());
			String contentOriginal = "";

			String content = "";
			Collections.reverse(lines);
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i) + "";
				if (line.contains(" " + methodName + "(") && line.contains(") {")) {
					int index = lines.indexOf(line);
					while (!lines.get(index + 1).contains("/*@")) {
						lines.remove(index + 1);
					}
					content = "    @ public normal_behavior\n    @ requires " + methodPreCondition + ";\n    @ ensures "
							+ methodPostCondition + ";\n    " + assignableString + "\n" + "    @*/\n"
							+ methodStub.substring(0, methodStub.indexOf("{") + 1) + "\n" + content;
					contentOriginal = line + "\n" + contentOriginal;
				} else {
					content = line + "\n" + content;
					contentOriginal = line + "\n" + contentOriginal;
				}
			}
			fileHandler.generateComposedClass(uri, className, className, content, contentOriginal);
			round++;
			if (originalNecessary) {
				methodName = "original_" + refinementsOriginal.get(0).getMethodName();
				className = refinementsOriginal.get(0).getClassName();
				refinements = refinementsOriginal;
				if (round > 1) originalNecessary = false;
			}
		}
		return;
	}

	String composeContractForCalledOriginal(List<CbCFormula> refinements, String keyword) {
		String composedCondition = "";
		if (keyword.equals("requires")) {
			composedCondition = Parser.rewriteConditionToJML(Parser.getConditionFromCondition(refinements.get(0).getStatement().getPreCondition().getName()).replace("\n", "").replace("\r", ""));
		} else if (keyword.equals("ensures")) {
			composedCondition = Parser.rewriteConditionToJML(Parser.getConditionFromCondition(refinements.get(0).getStatement().getPostCondition().getName()).replace("\n", "").replace("\r", ""));
		}

		CompositionTechnique compTechnique = refinements.get(0).getCompositionTechnique();
		for (int i = 1; i < refinements.size(); i++) {
			if (i != 1) {
				compTechnique = refinements.get(i - 1).getCompositionTechnique();
			}
			String conditionOriginal = "";
			if (keyword.equals("requires")) {
				conditionOriginal = Parser.rewriteConditionToJML(Parser.getConditionFromCondition(refinements.get(i).getStatement().getPreCondition().getName()).replace("\n", "").replace("\r", ""));
			} else if (keyword.equals("ensures")) {
				conditionOriginal = Parser.rewriteConditionToJML(Parser.getConditionFromCondition(refinements.get(i).getStatement().getPostCondition().getName()).replace("\n", "").replace("\r", ""));
			}
			composedCondition = applyCompositionTechnique(composedCondition, conditionOriginal, compTechnique);
			if (compTechnique == CompositionTechnique.CONTRACT_OVERRIDING || (compTechnique == CompositionTechnique.EXPLICIT_CONTRACTING && !composedCondition.contains(REGEX_ORIGINAL))) {
				return composedCondition;
			}
		}
		return composedCondition;
	}

	private static String resolveResultKeyword(String condition, JavaVariable returnVariable) {
		if (returnVariable != null) {
			String variableName = returnVariable.getName().substring(returnVariable.getName().indexOf(" "));
			return Parser.rewriteJMLConditionToKeY(condition.replaceAll(REGEX_RESULT, variableName));
		}
		return Parser.rewriteJMLConditionToKeY(condition);
	}

	private static CbCFormula getCbCFormula(AbstractStatement statement) {
		EObject parent = getParentOfStatement(statement);
		if (parent != null) {
			if (parent instanceof CbCFormula) {
				return (CbCFormula) parent;
			} else {
				return getCbCFormula((AbstractStatement) parent);
			}
		}
		return null;
	}

	private static EObject getParentOfStatement(AbstractStatement statement) {
		EObject parent = null;
		if (statement.getParent() != null) {
			parent = statement.getParent().eContainer();
		} else if (statement.eContainer() != null) {
			parent = statement.eContainer();
		}
		return parent;
	}

	public boolean proveWithKey(File location, boolean inlining) {
		try {
			return proveWithKey(location, monitor, inlining, formula, statement, problem, uri);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}			
	}
	
	public static boolean proveWithKey(File location, IProgressMonitor monitor, boolean inlining, CbCFormula formula, AbstractStatement statement, String problem, String uri) throws Exception {
		Proof proof = null;
		proof = KeYInteraction.startKeyProof(location, null, inlining, formula, statement, problem, uri);
		if (proof != null) {
			boolean closed = proof.openGoals().isEmpty();
			if (!closed) {
				Console.println("  Proof could not be closed.");
				CounterExampleGenerator generator = new CounterExampleGenerator();
				if (Settings.canRead()) {
					Settings settings = Settings.get();
					if (settings.getCounterExamples()) {
						generator.calculateExample(proof);
					}
				}
			} else {
				Console.println("  Proof is closed: " + closed + "\n");
				return closed;
			}
		}
		return false;
	}

	public boolean proveCImpliesCWithKey(Condition preCondition, Condition postCondition) {
		subProofName = "precondition";
		File location = createProveCImpliesCWithKey(preCondition.getName(), postCondition.getName(), true);
		Console.println("  Verify Pre -> Invariant");
		return proveWithKey(location, false);
	}
	
	private String getMethodName(String uri) {
		uri = uri.replaceAll("\\\\", "/");
		return uri.substring(uri.lastIndexOf("/") + 1, uri.length()).split("\\.")[0];
	}
	
	private void addExistingVarsTo(JavaVariables targetVars) {
		JavaVariables copyVars = EcoreUtil.copy(vars);
		var callingClass = FeatureUtil.getInstance().getCallingClass(uri);
		addNewVars(targetVars, copyVars);
		if (callingClass.isEmpty()) return;
		targetVars.getParams().addAll(copyMethodParams(uri, getMethodName(uri), callingClass));
	}
	
	private void addNewVars(final JavaVariables target, final JavaVariables source) {
		boolean found = false;
		for (int i = 0; i < source.getVariables().size(); i++) {
			for (int j = 0; j < target.getVariables().size(); j++) {
				if (target.getVariables().get(j).getName().equals(source.getVariables().get(i).getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				target.getVariables().add(source.getVariables().get(i));
				i--;
			}
			found = false;
		}
		for (int i = 0; i < source.getFields().size(); i++) {
			for (int j = 0; j < target.getFields().size(); j++) {
				if (target.getFields().get(j).getName().equals(source.getFields().get(i).getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				target.getFields().add(source.getFields().get(i));
				i--;
			}
			found = false;
		}
	}

	public File createProveCImpliesCWithKey(String preCondition, String postCondition, boolean override) {
		KeYFileContent content = new KeYFileContent(fileHandler.getProjectLocation(uri));
		content.setSrcFolder(sourceFolder);
		var callingClass = FeatureUtil.getInstance().getCallingClass(uri);
		JavaVariables varsFromJavaClass = readFieldsFromClass(callingClass);
		addExistingVarsTo(varsFromJavaClass);
		content.readVariables(varsFromJavaClass);
		content.readGlobalConditions(conds);

		//content.readInvariants(readInvariantsFromClass(uri.split("/")[4]));
		content.setPreFromCondition(preCondition + applyLiskovInheritance(preCondition, Parser.getConditionFromCondition(formula.getStatement().getPreCondition().getName()), "pre"));
		content.setPostFromCondition(postCondition + applyLiskovInheritance(postCondition, Parser.getConditionFromCondition(formula.getStatement().getPostCondition().getName()), "post"));
		content.rename(renaming);
		content.addSelf(formula);
		
		problem = content.getKeYCImpliesCContent();

		String location = fileHandler.getLocationString(uri) + configName;
		File keyFile = fileHandler.writeFile(problem, location, override, statement, subProofName);
		return keyFile;
	}

	public boolean provePostRepetitionWithKey(Condition invariant, Condition guard, Condition postCondition) {
		subProofName = "postcondition";
		String pre = invariant.getName() + " & !(" + guard.getName() + ")";
		File location = createProveCImpliesCWithKey(pre, postCondition.getName(), true);
		Console.println("  Verify (Invariant & !Guard) -> Post");
		return proveWithKey(location, false);
	}

	public boolean provePreSelWithKey(EList<Condition> guards, Condition preCondition) {
		String guardString = "";
		if (guards != null && guards.get(0) != null) {
			guardString = "((" + guards.get(0).getName() + ")";
			for (int i = 1; i < guards.size(); i++) {
				guardString += " | (" + guards.get(i).getName() + ")";
			}
			guardString += ")";
		} else {
			guardString = "true";
		}
		File location = createProveCImpliesCWithKey(preCondition.getName(), guardString, true);
		Console.println("  Verify Pre -> GvGvG...");
		return proveWithKey(location, false);
	}

	public boolean proveVariantWithKey(String code, Condition invariant, Condition guard, Variant variant) {
		subProofName = "variant";
		File location = createProveVariantWithKey(code, invariant, guard, variant, true);
		Console.println("Verify Pre -> {WhileStatement} (variant<variant0 & variant >= 0)");
		return proveWithKey(location, false);
	}

	public File createProveVariantWithKey(String code, Condition invariant, Condition guard, Variant variant, boolean override) {
		KeYFileContent content = new KeYFileContent(fileHandler.getProjectLocation(uri));
		content.setSrcFolder(sourceFolder);
		var callingClass = FeatureUtil.getInstance().getCallingClass(uri);
		JavaVariables varsFromJavaClass = readFieldsFromClass(callingClass);
		addExistingVarsTo(varsFromJavaClass);
		content.readVariables(varsFromJavaClass);
		content.addVariable("int variant");
		content.readGlobalConditions(conds);
		content.setPreFromCondition(invariant.getName() + " & " + guard.getName());
		content.setVariantPost(variant.getName());
		content.setStatement(code);
		content.rename(renaming);
		content.addSelf(formula);
		
		problem = content.getKeYStatementContent();

		String location = fileHandler.getLocationString(uri) + configName;
		File keyFile = fileHandler.writeFile(problem, location, override, statement, subProofName);
		return keyFile;
	}

	public String proveUseWeakestPreWithKey() {
		File location = createProveUseWeakestPreWithKey(true);
		Console.println("Verify Pre -> {Statement} Post");
		return createWPWithKey(location);
	}

	private File createProveUseWeakestPreWithKey(boolean override) {
		KeYFileContent content = new KeYFileContent(fileHandler.getProjectLocation(uri));
		content.setSrcFolder(sourceFolder);
		content.readVariables(EcoreUtil.copy(vars));
		content.readGlobalConditions(conds);
		content.readInvariants(readInvariantsFromClass(uri.split("/")[4]));
		content.setStatement(statement.getName());
		content.setPostFromCondition("(" + statement.getPostCondition().getName() + applyLiskovInheritance(statement.getPostCondition().getName(), Parser.getConditionFromCondition(formula.getStatement().getPreCondition().getName()), "pre") + ")");
		content.addSelf(formula);
		content.rename(renaming);

		problem = content.getKeYStatementContent();

		String location = fileHandler.getLocationString(uri) + configName;
		File keyFile = fileHandler.writeFile(problem, location, override, statement, subProofName);
		return keyFile;
	}

	public String createWPWithKey(File location) {
		Proof proof = KeYInteraction.startKeyProof(location, monitor, false, formula, statement, problem, uri);
		if (proof != null) {
			String wp = "";
			Iterator<Goal> it = proof.openGoals().iterator();
			Goal goal = it.next();
			String[] goalString = goal.toString().split("==>");
			String antecedent = goalString[0].trim();
			String succedent = goalString[1].trim();
			wp += "(";
			if (antecedent.isEmpty()) {
				wp += succedent;
			} else {
				if (succedent.isEmpty()) {
					wp += "!(" + antecedent + ")";
				} else {
					wp += antecedent + " -> " + succedent;
				}
			}
			wp += ")";
			if (it.hasNext()) {
				goal = it.next();
				goalString = goal.toString().split("==>");
				antecedent = goalString[0].trim();
				succedent = goalString[1].trim();
				wp += " & (";
				if (antecedent.isEmpty()) {
					wp += succedent;
				} else {
					if (succedent.isEmpty()) {
						wp += "!" + antecedent;
					} else {
						wp += antecedent + " -> " + succedent;
					}
				}
				wp += ")";
			}
			Console.println("  Weakest precondition is: " + wp);
			return wp;
		}
		return "";
	}

	public void setConfigName(String configName) {
		this.configName = "/" + configName;		
	}
}
