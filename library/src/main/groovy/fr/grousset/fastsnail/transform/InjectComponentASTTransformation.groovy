package fr.grousset.fastsnail.transform

import groovyjarjarasm.asm.Opcodes
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ExpressionTransformer
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import fr.grousset.fastsnail.AndroidUtils

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class InjectComponentASTTransformation implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
        }

        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode node = (AnnotationNode) nodes[0]
        if (parent instanceof FieldNode) {
            final FieldNode fieldNode = (FieldNode) parent

            // Determine name
            String name
            final Expression nameExpr = node.getMember("name")
            if(nameExpr != null && nameExpr instanceof ConstantExpression) {
                name = (String) ((ConstantExpression)nameExpr).getValue()
            } else{
                name = AndroidUtils.extractNameFromMember(fieldNode.name)
            }

            // Generate initial value
            fieldNode.initialValueExpression = generateInitialValue(name)
        }


    }

    private Expression generateInitialValue(String name) {

        def ast = new AstBuilder().buildFromSpec {
            staticMethodCall(fr.grousset.fastsnail.ObjectGraph, 'injectComponent') {
                argumentList {
                    constant name
                }
            }
        }

        return ast[0]

    }


}