package pa.iscde.javadoc.generator;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Wrapper do FieldDeclaration 
 * Tem metodos auxiliar para extrair informação necessária a apresentar no JavaDoc gerado
 *
 * @author Miguel
 */
public class FieldDeclarationWrapper {

    private String modifier;
    private ObjectMap varMap;

    public FieldDeclarationWrapper(FieldDeclaration field) {
	this.modifier = extractModifier(field);
	this.varMap = extractType(field);
    }

    public String getModifier() {
	return modifier;
    }

    public ObjectMap getVarMap() {
	return varMap;
    }

    private String extractModifier(FieldDeclaration field) {
	return field.modifiers().get(0).toString();
    }

    /**
     * Extrai o Tipo do Atributo. Sabe extrair se for Simples, um Array e se estiver parameterizado numa Lista. 
     * @param field
     * @return
     */
    private ObjectMap extractType(FieldDeclaration field) {
	VariableDeclarationFragment var = (VariableDeclarationFragment) field.fragments().get(0);
	String varType = field.getType().toString();
	String varName = var.getName().toString();
	String clazz = "";

	if (field.getType() instanceof ArrayType) {
	    ArrayType x = ((ArrayType) field.getType());
	    clazz = x.getElementType().toString();
	} else if (field.getType() instanceof SimpleType) {
	    SimpleType x = ((SimpleType) field.getType());
	    clazz = x.toString();
	} else if (field.getType() instanceof ParameterizedType) { // Listas
	    ParameterizedType x = ((ParameterizedType) field.getType());
	    clazz = x.typeArguments().get(0).toString();
	}

	String path = CacheObjectMapper.getInstance().getFilePath(clazz);

	// Substituição por caracteres similares, umas vez que os caracteres '<'e '>', são delimitdores de String na Template
	varType = varType.replace('<', '≤');
	varType = varType.replace('>', '≥');
	ObjectMap varMap = new ObjectMap(varType, varName, path);

	return varMap;
    }
}