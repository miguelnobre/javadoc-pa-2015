package pa.iscde.javadoc.internal;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import extensionpoints.ISearchEvent;
import extensionpoints.ISearchEventListener;
import pa.iscde.javadoc.service.JavaDocServices.Type;
import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.javaeditor.service.JavaEditorListener;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;

public class JavaDocView implements PidescoView {

    private static final String NAVIGATION_BAR = "<b>Menu de Navegação: </b><a href='#back'>Voltar</a> <a href='#next'>Seguinte</a> <br>";

    private static JavaDocView instance = null;

    private Composite viewArea;
    private Browser browser;

    private File lastParsedFile;
    private String lastGeneratedText;

    private Stack<String> stackNext = new Stack<String>();
    private Stack<String> stackPrevious = new Stack<String>();

    public static JavaDocView getInstance() {
	return instance;
    }

    @Override
    public void createContents(final Composite viewArea, final Map<String, Image> imageMap) {
	instance = this;
	this.viewArea = viewArea;

	FillLayout fillLayout = new FillLayout();
	fillLayout.type = SWT.VERTICAL;
	viewArea.setLayout(fillLayout);

	this.browser = new Browser(viewArea, SWT.NONE);
	this.browser.setText("<h1>JavaDoc View</h1>");

	this.viewArea.getParent();

	browser.addLocationListener(new LocationListener() {
	    @Override
	    public void changing(LocationEvent event) {
	    }

	    @Override
	    public void changed(LocationEvent event) {
		// Evento para seleccionar o codigo do metodo
		if (event.location.contains("#") && event.location.contains("-")) {
		    if (null != lastParsedFile
			    && lastParsedFile.equals(JavaDocServiceLocator.getJavaEditorService().getOpenedFile())) {
			String location[] = event.location.substring(event.location.indexOf("#") + 1).split("-");
			JavaDocServiceLocator.getJavaEditorService().selectText(lastParsedFile,
				Integer.valueOf(location[0]), Integer.valueOf(location[1]));
		    }
		} // Evento para Navegar no JavaDoc
		else if (event.location.contains("#") && event.location.contains(".java")) {
		    String path = event.location.substring(event.location.indexOf("#") + 1);
		    File file = new File(path);
		    JavaDocServiceLocator.getJavaEditorService().openFile(file);

		} // Evento para Voltar na Navegaçao de Classes
		else if (event.location.contains("#back")) {
		    if (stackPrevious.size() > 1) {
			stackNext.push(stackPrevious.pop());
			File file = new File(stackPrevious.pop());
			JavaDocServiceLocator.getJavaEditorService().openFile(file);
		    }
		} // Evento para Avançar na Navegacao de Classes
		else if (event.location.contains("#next")) {
		    if (!stackNext.isEmpty()) {
			File file = new File(stackNext.pop());
			JavaDocServiceLocator.getJavaEditorService().openFile(file);
		    }
		} else if (event.location.contains("http")) {
		    try {
			URL url = new URL(event.location);
			Desktop.getDesktop().browse(url.toURI());
			// Como o browser abre a pagina do link, é necessário
			// fazer parse de novo a classe, para que o Browser
			// volte a apresentar o JavaDoc.
			JavaDocServiceLocator.getJavaEditorService().openFile(lastParsedFile);
		    } catch (IOException e) {
			e.printStackTrace();
		    } catch (URISyntaxException e) {
			e.printStackTrace();
		    }
		}
	    }
	});

	final JavaEditorListener javaEditorListener;
	final JavaEditorServices javaEditorServices = JavaDocServiceLocator.getJavaEditorService();
	if (null != javaEditorServices) {
	    javaEditorServices.addListener(javaEditorListener = new JavaEditorListener() {
		@Override
		public void selectionChanged(File file, String text, int offset, int length) {
		}

		@Override
		public void fileSaved(File file) {
		}

		@Override
		public void fileOpened(File file) {
		    generateJavadoc(file);
		}

		@Override
		public void fileClosed(File file) {
		}
	    });
	} else {
	    javaEditorListener = null;
	}

	final ISearchEventListener iSearchEventListener;
	final ISearchEvent searchService = JavaDocServiceLocator.getSearchService();
	if (null != searchService) {
	    searchService.addListener(iSearchEventListener = new ISearchEventListener() {
		@Override
		public void widgetSelected(String text_Search, String text_SearchInCombo,
			String specificText_SearchInCombo, String text_SearchForCombo,
			ArrayList<String> buttonsSelected_SearchForCombo) {
		    if (lastGeneratedText != null) {
			setJavadocText(lastGeneratedText.replace(text_Search, "<mark>" + text_Search + "</mark>"));
		    }
		}
	    });
	} else {
	    iSearchEventListener = null;
	}

	viewArea.addDisposeListener(new DisposeListener() {
	    @Override
	    public void widgetDisposed(DisposeEvent e) {
		instance = null;
		if (null != javaEditorListener && null != javaEditorListener) {
		    javaEditorServices.removeListener(javaEditorListener);
		}
		if (null != searchService && null != iSearchEventListener) {
		    searchService.removeListener(iSearchEventListener);
		}
	    }
	});

	File openedFile;
	if (null != (openedFile = JavaDocServiceLocator.getJavaEditorService().getOpenedFile())) {
	    generateJavadoc(openedFile);
	}

    }

    public static void closeView() {
	if (null != instance) {
	    instance.viewArea.dispose();
	}
    }

    private void generateJavadoc(final File openedFile) {
	if (null != openedFile) {

	    lastParsedFile = openedFile;
	    lastGeneratedText = JavaDocActivator.getJavaDocService().render(openedFile);

	    Pattern p = Pattern.compile("^", Pattern.MULTILINE);
	    Matcher m = p.matcher(lastGeneratedText);
	    StringBuffer sb = new StringBuffer();
	    int line = 0;
	    while (m.find()) {
		m.appendReplacement(sb, "<a name='L" + (line++) + "'></a>$0");
	    }
	    m.appendTail(sb);

	    lastGeneratedText = sb.toString();

	    setJavadocText(lastGeneratedText);

	    if (stackPrevious.isEmpty() || !lastParsedFile.getAbsolutePath().equals(stackPrevious.peek())) {
		stackPrevious.push(lastParsedFile.getAbsolutePath());
	    }
	}
    }

    private String addNavigationBar(final String javadoc) {
	return NAVIGATION_BAR + javadoc;
    }

    private void setJavadocText(final String javadoc) {
	this.browser.setText(addNavigationBar(javadoc));
    }

    public String getLastGeneratedText() {
	return lastGeneratedText;
    }

    public void jumpToSearchPosition(String textSearch, int line) {
	this.browser.evaluate("window.location.hash = 'L" + line + "';", true);
    }
}