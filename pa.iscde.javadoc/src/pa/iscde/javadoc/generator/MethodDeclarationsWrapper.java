package pa.iscde.javadoc.generator;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public class MethodDeclarationsWrapper {

	private String modifier;
	private String returnType;
	private String name;
	private String params;
	private String throwsType;
	private boolean isConstructor;

	public MethodDeclarationsWrapper(MethodDeclaration method) {
		this.modifier = getModifier(method);
		this.returnType = getReturn(method);
		this.name = getName(method);
		this.params = getParams(method);
		this.throwsType = getThrows(method);
		this.isConstructor = method.isConstructor();
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

	public boolean getIsConstructor() {
		return this.isConstructor;
	}

	public String getSignature() {
		return this.modifier + " " +  (!this.isConstructor ? this.returnType + " ": "")  + this.name + this.params + (this.throwsType.length() > 0 ? " throws " + this.throwsType : "");
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
	
	private String getParams(MethodDeclaration method) {
		StringBuilder sb = new StringBuilder("(");
		
		for (int i = 0; i < method.parameters().size(); i++) {
			sb.append(method.parameters().get(i) + (i != method.parameters().size() - 1 ? ", " : ""));
		}
		System.out.println(sb.toString());
		return sb.append(")").toString();
	}

	private String getReturn(MethodDeclaration method) {

		if (method.getReturnType2() != null) {
			return method.getReturnType2().toString();
		}
		return null;
	}

	private String getThrows(MethodDeclaration method) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < method.thrownExceptionTypes().size(); i++) {
			sb.append(method.thrownExceptionTypes().get(i).toString() + (i != method.thrownExceptionTypes().size() - 1 ? ", " : ""));
		}

		return sb.toString();
	}
}