package pa.iscde.javadoc.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

public class MethodDeclarationsWrapper {

	private String modifier;
	private String returnType;
	private String name;
	private String params;
	private String throwsType;
	private Boolean constructor;
	
	private ObjectMap returnMap;
	private List<ObjectMap> variableType = new ArrayList<ObjectMap>();
	private List<ObjectMap> throwsMap = new ArrayList<ObjectMap>();
	
	public MethodDeclarationsWrapper(MethodDeclaration method, Map<String, File> workSpaceFiles) {
		this.modifier = getModifier(method);
		this.returnType = getReturn(method, workSpaceFiles);
		this.name = getName(method);
		this.params = getParams(method, workSpaceFiles);
		this.throwsType = getThrows(method, workSpaceFiles);
		this.constructor = method.isConstructor();
	}

	public String getModifier() {
		return this.modifier;
	}

	public String getName() {
		return this.name;
	}

	public String getReturnType() {
		return this.returnType;
	}

	public String getThrowsType() {
		return this.throwsType;
	}

	public boolean getConstructor() {
		return this.constructor;
	}

	public String getParams() {
		return this.params;
	}

	public List<ObjectMap> getVariableType() {
		return this.variableType;
	}
	
	public ObjectMap getReturnMap() {
		return this.returnMap;
	}
	
	public List<ObjectMap> getThrowsMap() {
		return this.throwsMap;
	}
	

	public String getSignature() {
		return this.modifier + " " + (!this.constructor ? this.returnType + " " : "") + this.name + this.params
				+ (this.throwsType.length() > 0 ? " throws " + this.throwsType : "");
	}

	private String getModifier(MethodDeclaration method) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < method.modifiers().size(); i++) {
			if (!method.modifiers().get(i).toString().startsWith("@")) {
				sb.append(method.modifiers().get(i) + (i != method.modifiers().size() - 1 ? " " : ""));
			}
		}

		return sb.toString();
	}

	private String getName(MethodDeclaration method) {
		return method.getName().toString();
	}

	private String getParams(MethodDeclaration method, Map<String, File> workSpaceFiles) {
		StringBuilder sb = new StringBuilder("(");

		for (int i = 0; i < method.parameters().size(); i++) {
			sb.append(method.parameters().get(i) + (i != method.parameters().size() - 1 ? ", " : ""));

			SingleVariableDeclaration variable = (SingleVariableDeclaration) method.parameters().get(i);

			// if (workSpaceFiles.containsKey(variable.getType() + ".java")) {
			String path = workSpaceFiles.get(variable.getType() + ".java") != null ? workSpaceFiles.get(variable.getType() + ".java").getAbsolutePath() : null;
			ObjectMap p = new ObjectMap(variable.getType().toString(), variable.getName().toString(), path);
			this.variableType.add(p);
			// }

		}
		return sb.append(")").toString();
	}

	private String getReturn(MethodDeclaration method, Map<String, File> workSpaceFiles) {

		if (method.getReturnType2() != null) {
			String path = workSpaceFiles.get(method.getReturnType2().toString() + ".java") != null ? workSpaceFiles.get(method.getReturnType2().toString() + ".java").getAbsolutePath() : null;
			this.returnMap = new ObjectMap(method.getReturnType2().toString(), null, path);
			return method.getReturnType2().toString();
		}
		return null;
	}

	private String getThrows(MethodDeclaration method, Map<String, File> workSpaceFiles) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < method.thrownExceptionTypes().size(); i++) {
			sb.append(method.thrownExceptionTypes().get(i).toString() + (i != method.thrownExceptionTypes().size() - 1 ? ", " : ""));
			
			String path = workSpaceFiles.get(method.thrownExceptionTypes().get(i) + ".java") != null ? workSpaceFiles.get(method.thrownExceptionTypes().get(i) + ".java").getAbsolutePath() : null;
			ObjectMap ex = new ObjectMap(method.thrownExceptionTypes().get(i).toString(), null, path);
			this.throwsMap.add(ex);
		}

		return sb.toString();
	}
}