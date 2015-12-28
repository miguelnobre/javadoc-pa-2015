package pa.iscde.javadoc.generator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 * Wrapper do MethodDeclaration
 * Tem metodos auxiliar para extrair informação necessária a apresentar no JavaDoc gerado
 *
 * @author Miguel
 */
public class MethodDeclarationsWrapper {

    private Boolean constructor;
    private String modifier;
    private ObjectMap returnMap;
    private String name;
    private List<ObjectMap> variableType = new ArrayList<ObjectMap>();
    private List<ObjectMap> throwsMap = new ArrayList<ObjectMap>();

    public MethodDeclarationsWrapper(MethodDeclaration method) {
	this.constructor = method.isConstructor();
	this.modifier = getModifier(method);
	this.returnMap = getReturn(method);
	this.name = getName(method);
	this.variableType = getParams(method);
	this.throwsMap = getThrows(method);
    }

    public boolean getConstructor() {
	return this.constructor;
    }

    public String getModifier() {
	return this.modifier;
    }

    public ObjectMap getReturnMap() {
	return this.returnMap;
    }

    public String getName() {
	return this.name;
    }

    public List<ObjectMap> getVariableType() {
	return this.variableType;
    }

    public List<ObjectMap> getThrowsMap() {
	return this.throwsMap;
    }

    /**
     * Extrai o Modifier do Metodo. Ignora anotações como o "@Override".
     * @param method
     * @return
     */
    private String getModifier(MethodDeclaration method) {
	for (int i = 0; i < method.modifiers().size(); i++) {
	    if (!method.modifiers().get(i).toString().startsWith("@")) { // Despreza anotações
		return method.modifiers().get(i).toString();
	    }
	}
	return null;
    }

    /**
     * Extrai o nome do Metodo
     * @param method
     * @return
     */
    private String getName(MethodDeclaration method) {
	return method.getName().toString();
    }

    /**
     * Extrai os Parametros do Metodo e obtem o respectivo caminho do ficheiro da classe no Workspace
     * @param method
     * @return
     */
    private List<ObjectMap> getParams(MethodDeclaration method) {
	List<ObjectMap> varList = new ArrayList<ObjectMap>();

	for (Object parameter : method.parameters()) {
	    SingleVariableDeclaration variable = (SingleVariableDeclaration) parameter;

	    String path = CacheObjectMapper.getInstance().getFilePath(variable.getType().toString());
	    ObjectMap object = new ObjectMap(variable.getType().toString(), variable.getName().toString(), path);
	    varList.add(object);
	}
	return varList;
    }

    /**
     * Extrai o Tipo de Return do Metodo, e obtem o respectivo caminho do ficheiro da classe no Workspace
     * @param method
     * @return
     */
    private ObjectMap getReturn(MethodDeclaration method) {

	if (method.getReturnType2() != null) {
	    String path = CacheObjectMapper.getInstance().getFilePath(method.getReturnType2().toString());
	    return new ObjectMap(method.getReturnType2().toString(), null, path);
	}
	return null;
    }

    /**
     * Extrai as Excepções que o Metodo lança, e obtem o respectivo caminho do ficheiro da classe no Workspace
     * @param method
     * @return
     */
    private List<ObjectMap> getThrows(MethodDeclaration method) {
	List<ObjectMap> throwList = new ArrayList<ObjectMap>();

	for (Object throwExceptionType : method.thrownExceptionTypes()) {
	    String path = CacheObjectMapper.getInstance().getFilePath(throwExceptionType.toString());
	    ObjectMap throwException = new ObjectMap(throwExceptionType.toString(), null, path);
	    throwList.add(throwException);
	}

	return throwList;
    }
}