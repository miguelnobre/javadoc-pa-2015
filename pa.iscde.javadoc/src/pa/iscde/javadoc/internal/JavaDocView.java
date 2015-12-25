package pa.iscde.javadoc.internal;

import java.io.File;
import java.util.Map;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import pa.iscde.javadoc.generator.StringTemplateVisitor;
import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.javaeditor.service.JavaEditorListener;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;

public class JavaDocView implements PidescoView {

	private static JavaDocView instance = null;

	private Composite viewArea;
	private Browser browser;

	private File lastParsedFile;

	public static Stack<String> stackNext = new Stack<String>();
	public static Stack<String> stackPrevious = new Stack<String>();

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

		// Button button = new Button(viewArea, SWT.NONE);
		// button.setText("Export");
		// button.addMouseListener(new MouseListener() {
		//
		// @Override
		// public void mouseUp(MouseEvent e) {
		// }
		//
		// @Override
		// public void mouseDown(MouseEvent e) {
		// }
		//
		// @Override
		// public void mouseDoubleClick(MouseEvent e) {
		// }
		// });
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
					if (null != lastParsedFile	&& lastParsedFile.equals(JavaDocServiceLocator.getJavaEditorService().getOpenedFile())) {
						String location[] = event.location.substring(event.location.indexOf("#") + 1).split("-");
						JavaDocServiceLocator.getJavaEditorService().selectText(lastParsedFile,	Integer.valueOf(location[0]), Integer.valueOf(location[1]));
					}
					// Evento para Navegar no JavaDoc
				} else if (event.location.contains("#") && event.location.contains(".java")) {
					String path = event.location.substring(event.location.indexOf("#") + 1);
					File file = new File(path);
					JavaDocServiceLocator.getJavaEditorService().openFile(file);
					// Evento para Voltar a ultima classe
				} else if (event.location.contains("#back")) {
					if (stackPrevious.size() > 1) {
						stackNext.push(stackPrevious.pop());
						//Remove da stack a classe actual, para no proximo pop obtermos a classse anteriormente aberta
						File file = new File(stackPrevious.pop());
						JavaDocServiceLocator.getJavaEditorService().openFile(file);
					}
				} else if (event.location.contains("#next")) {
					if (!stackNext.isEmpty()) {
						File file = new File(stackNext.pop());
						JavaDocServiceLocator.getJavaEditorService().openFile(file);
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

		viewArea.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				instance = null;
				if (null != javaEditorListener) {
					javaEditorServices.removeListener(javaEditorListener);
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

	public void generateJavadoc(final File openedFile) {
		if (null != openedFile) {
			StringBuilder sb = new StringBuilder();
			StringTemplateVisitor jDoc = new StringTemplateVisitor(sb);
			JavaDocServiceLocator.getJavaEditorService().parseFile(openedFile, jDoc);
			String HTML = "<b>Menu de Navegação: </b><a href='#back'>Voltar</a> <a href='#next'>Seguinte</a> <br>" + sb.toString();
			this.browser.setText(HTML);
			lastParsedFile = openedFile;
			if (stackPrevious.isEmpty() || !lastParsedFile.getAbsolutePath().equals(stackPrevious.peek())) {
				stackPrevious.push(lastParsedFile.getAbsolutePath());
			}
		}
	}

}