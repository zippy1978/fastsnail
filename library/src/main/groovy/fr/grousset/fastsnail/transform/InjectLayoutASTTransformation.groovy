package fr.grousset.fastsnail.transform

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class InjectLayoutASTTransformation implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
        }

        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode node = (AnnotationNode) nodes[0]

        ClassNode cNode = (ClassNode)parent

        // Value expression
        final Expression valueExpr = node.getMember("value")

        // Activity
        if (AstUtils.inheritsFromClass(cNode, Activity.class)) {

            cNode.methods.each {methodNode ->

                if (methodNode.name == 'onCreate') {
                    this.modifyActivityOnCreate(methodNode, valueExpr)
                }
            }
        }

        // Fragment
        if (AstUtils.inheritsFromClass(cNode, Fragment.class)) {

            cNode.methods.each {methodNode ->

                if (methodNode.name == 'onCreateView') {
                    this.modifyFragmentOnCreateView(methodNode, valueExpr)
                }
            }
        }


    }

    private void modifyFragmentOnCreateView(MethodNode methodNode, Expression valueExpression) {

        if (methodNode.code instanceof BlockStatement) {

            BlockStatement blockStatement = methodNode.code as BlockStatement

            int returnPosition = AstUtils.findReturnPosition(blockStatement)

            // Remove existing return
            if (returnPosition >= 0) {
                blockStatement.statements.remove(returnPosition)
            }

            // Add return inflate in two parts : assignment (first expression) and return (last)
            blockStatement.statements.add(0, new ExpressionStatement(
                    new DeclarationExpression(new VariableExpression('fastSnailFragmentView'),
                            Token.newSymbol(Types.EQUAL, 0, 0), this.createFragmentInflateCall(methodNode, valueExpression)))
            )
            blockStatement.statements.add(new ReturnStatement(new VariableExpression('fastSnailFragmentView')))



        }
    }

    private Expression createFragmentInflateCall(MethodNode methodNode, Expression valueExpression) {

        Parameter[] params = methodNode.parameters

        String inflaterName = params[0].name
        String containerName = params[1].name

        return new MethodCallExpression(new VariableExpression(inflaterName), 'inflate',
                new ArgumentListExpression(valueExpression, new VariableExpression(containerName), new ConstantExpression(false)))

    }

    private void modifyActivityOnCreate(MethodNode methodNode, Expression valueExpression) {

        if (methodNode.code instanceof BlockStatement) {

            BlockStatement blockStatement = methodNode.code as BlockStatement

            int superCallPosition = AstUtils.findMethodCallPosition(blockStatement, 'super', 'onCreate')

            int injectionPosition = superCallPosition + 1

            blockStatement.statements.add(injectionPosition, new ExpressionStatement(this.createActivitySetContentViewCall(valueExpression)))
        }
    }

    private Expression createActivitySetContentViewCall(Expression valueExpression) {

        return new MethodCallExpression(new VariableExpression('this'), 'setContentView', valueExpression)

    }


}