package de.kit.tva.lost.views;

import java.util.ArrayList;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import de.kit.tva.lost.controllers.CodeController;
import de.kit.tva.lost.controllers.TranslatorController;
import de.kit.tva.lost.controllers.UiController;
import de.kit.tva.lost.interfaces.Listener;
import de.kit.tva.lost.interfaces.View;
import de.kit.tva.lost.models.CodeModel;
import de.kit.tva.lost.models.DiagramTranslator;
import de.kit.tva.lost.models.TranslatorModel;
import de.kit.tva.lost.models.UiModel;

public class LostUiView extends Composite implements View, Listener {
    private LocalResourceManager localResourceManager;
    private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());

    private Composite grpHeader;
    private Composite grpSyntax;
    private StyledText codeField;
    private Button btnHelp;
    private Button btnTranslate;
    private Button btnLoad;
    private Button btnBasic;
    private Button btnExtended;

    private ArrayList<Listener> listeners = new ArrayList<>();

    /**
     * Create the composite.
     * 
     * @param parent
     * @param style
     */
    public LostUiView(Composite parent, int style) {
	super(parent, style);
	createResourceManager();
	GridLayout gridLayout = new GridLayout(3, false);
	gridLayout.marginHeight = 0;
	setLayout(gridLayout);

	grpHeader = new Composite(this, SWT.NONE);
	GridData gdGrpHeader = new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1);
	gdGrpHeader.widthHint = 462;
	grpHeader.setLayoutData(gdGrpHeader);
	grpHeader.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
	formToolkit.adapt(grpHeader);
	formToolkit.paintBordersFor(grpHeader);
	GridLayout glGrpHeader = new GridLayout(3, false);
	glGrpHeader.marginTop = 5;
	glGrpHeader.marginHeight = 0;
	grpHeader.setLayout(glGrpHeader);

	grpSyntax = new Composite(this, SWT.NONE);
	grpSyntax.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
	formToolkit.adapt(grpSyntax);
	formToolkit.paintBordersFor(grpSyntax);

	btnHelp = new Button(this, SWT.FLAT);
	btnHelp.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	formToolkit.adapt(btnHelp, true, true);
	btnHelp.setText("?");

	/*
	 * Button btnExhaustive = new Button(grpSyntax, SWT.RADIO);
	 * btnExhaustive.setBounds(129, 0, 77, 16); formToolkit.adapt(btnExhaustive,
	 * true, true); btnExhaustive.setText("Exhaustive");
	 */

	btnBasic = new Button(grpSyntax, SWT.RADIO);
	btnBasic.setBounds(0, 0, 48, 16);
	formToolkit.adapt(btnBasic, true, true);
	btnBasic.setText("Basic");

	btnExtended = new Button(grpSyntax, SWT.RADIO);
	btnExtended.setSelection(true);
	btnExtended.setBounds(53, 0, 70, 16);
	formToolkit.adapt(btnExtended, true, true);
	btnExtended.setText("Extended");

	btnTranslate = new Button(grpHeader, SWT.TOGGLE);
	btnTranslate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	btnTranslate.setFont(localResourceManager.create(FontDescriptor.createFrom("Segoe UI", 8, SWT.NORMAL)));
	formToolkit.adapt(btnTranslate, true, true);
	btnTranslate.setText("LOST ⇉ CbC");

	btnLoad = new Button(grpHeader, SWT.TOGGLE);
	btnLoad.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
	btnLoad.setFont(localResourceManager.create(FontDescriptor.createFrom("Segoe UI", 8, SWT.NORMAL)));
	formToolkit.adapt(btnLoad, true, true);
	btnLoad.setText("LOST ⇇ CbC");

	codeField = new StyledText(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
	codeField.setFont(
		localResourceManager.create(FontDescriptor.createFrom("Cascadia Code SemiBold", 10, SWT.NORMAL)));
	codeField.setBottomMargin(5);
	codeField.setLeftMargin(5);
	codeField.setRightMargin(5);
	codeField.setTabStops(new int[] { 16 });
	codeField.setAlwaysShowScrollBars(true);
	GridData gdCodeField = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
	gdCodeField.heightHint = 216;
	codeField.setLayoutData(gdCodeField);
	formToolkit.adapt(codeField);
	formToolkit.paintBordersFor(codeField);
	Color uiBackground = new Color(238, 232, 213);
	Color codeBackground = new Color(253, 246, 227);
	Color codeForeground = new Color(0, 0, 0);
	setBackgroundRecursive(this, uiBackground);
	setBackgroundRecursive(grpHeader, uiBackground);
	setBackgroundRecursive(grpSyntax, uiBackground);
	this.codeField.setBackground(codeBackground);
//	this.codeField.setForeground(new Color(125, 132, 134));
	this.codeField.setForeground(codeForeground);
	initMVCs();
    }

    private void initMVCs() {
	// Models
	UiModel uiModel = new UiModel();
	CodeModel codeModel = new CodeModel();
	TranslatorModel translatorModel = new TranslatorModel();
	DiagramTranslator diagramTranslatorModel = new DiagramTranslator();
	// Views
	CodeView codeView = new CodeView(this.getCodeField());
	// Controllers
	new UiController(this, codeView, uiModel, codeModel);
	new CodeController(codeView, codeModel);
	new TranslatorController(this, codeView, codeModel, translatorModel, diagramTranslatorModel);
    }

    public LostUiView getUI() {
	return this;
    }

    public Composite getButtonsHeader() {
	return this.grpHeader;
    }

    public Composite getSyntaxHeader() {
	return this.grpSyntax;
    }

    public StyledText getCodeField() {
	return this.codeField;
    }

    public Button getHelpButton() {
	return this.btnHelp;
    }

    public Button getBasicViewButton() {
	return this.btnBasic;
    }

    public Button getExtendedViewButton() {
	return this.btnExtended;
    }

    public Button getTranslateButton() {
	return this.btnTranslate;
    }

    public Button getLoadButton() {
	return this.btnLoad;
    }

    private void createResourceManager() {
	localResourceManager = new LocalResourceManager(JFaceResources.getResources(), this);
    }

    @Override
    protected void checkSubclass() {
	// Disable the check that prevents subclassing of SWT components
    }

    public void addListener(Listener listener) {
	if (!listeners.contains(listener)) {
	    listeners.add(listener);
	}
    }

    public void removeListener(Listener listener) {
	if (listeners.contains(listener)) {
	    listeners.remove(listener);
	}
    }

    public void notifyListeners() {
	listeners.forEach(l -> l.update());
    }

    @Override
    public void update() {
    }

    private void setBackgroundRecursive(Composite composite, Color color) {
	composite.setBackground(color);
	for (int i = 0; i < composite.getChildren().length; i++) {
	    setColorOfChild(composite.getChildren()[i], color);
	}
    }

    private void setColorOfChild(Control control, Color color) {
	control.setBackground(color);
    }
}
