/**
 * generated by Xtext 2.10.0
 */
package de.tu_bs.cs.isf.cbc.textual.tool.generator;

import com.google.common.collect.Iterators;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCFormula;
import de.tu_bs.cs.isf.cbc.cbcmodel.CbCProblem;
import de.tu_bs.cs.isf.cbc.cbcmodel.GlobalConditions;
import de.tu_bs.cs.isf.cbc.cbcmodel.JavaVariables;
import de.tu_bs.cs.isf.cbc.cbcmodel.Renaming;
import java.io.IOException;
import java.util.Collections;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import org.eclipse.xtext.xbase.lib.Exceptions;

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
@SuppressWarnings("all")
public class DslGenerator extends AbstractGenerator {
  public void doGenerate(final Resource resource, final IFileSystemAccess2 fsa, final IGeneratorContext context) {
    JavaVariables vars = null;
    GlobalConditions conds = null;
    Renaming renaming = null;
    boolean _hasNext = Iterators.<JavaVariables>filter(resource.getAllContents(), JavaVariables.class).hasNext();
    if (_hasNext) {
      vars = Iterators.<JavaVariables>filter(resource.getAllContents(), JavaVariables.class).next();
    }
    boolean _hasNext_1 = Iterators.<GlobalConditions>filter(resource.getAllContents(), GlobalConditions.class).hasNext();
    if (_hasNext_1) {
      conds = Iterators.<GlobalConditions>filter(resource.getAllContents(), GlobalConditions.class).next();
    }
    boolean _hasNext_2 = Iterators.<Renaming>filter(resource.getAllContents(), Renaming.class).hasNext();
    if (_hasNext_2) {
      renaming = Iterators.<Renaming>filter(resource.getAllContents(), Renaming.class).next();
    }
    CbCFormula formula = Iterators.<CbCFormula>filter(resource.getAllContents(), CbCFormula.class).next();
    URI _uRI = resource.getURI();
    final TraverseFormulaAndGenerate traverser = new TraverseFormulaAndGenerate(vars, conds, renaming, _uRI, formula, resource);
    formula = traverser.traverseFormulaAndGenerate();
    final ResourceSet rs = new ResourceSetImpl();
    final String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
    URI uri = resource.getURI();
    uri = uri.trimFragment();
    uri = uri.trimFileExtension();
    uri = uri.appendFileExtension("cbcmodel");
    String _platformString = uri.toPlatformString(true);
    String _plus = (workspace + _platformString);
    final Resource r = rs.createResource(URI.createFileURI(_plus));
    r.getContents().addAll(resource.getContents());
    EObject _get = r.getContents().get(0);
    final CbCProblem problem = ((CbCProblem) _get);
    problem.setCbcformula(formula);
    try {
      r.save(Collections.EMPTY_MAP);
    } catch (final Throwable _t) {
      if (_t instanceof IOException) {
        final IOException e = (IOException)_t;
        e.printStackTrace();
      } else {
        throw Exceptions.sneakyThrow(_t);
      }
    }
  }
}
