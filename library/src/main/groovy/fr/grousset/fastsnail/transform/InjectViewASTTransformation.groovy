package fr.grousset.fastsnail.transform

import android.app.Activity
import android.app.Fragment
import org.codehaus.groovy.ast.*
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
public class InjectViewASTTransformation implements ASTTransformation {

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {

        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            throw new RuntimeException("Internal error: expecting [AnnotationNode, AnnotatedNode] but got: " + Arrays.asList(nodes));
        }

        AnnotatedNode parent = (AnnotatedNode) nodes[1]
        AnnotationNode node = (AnnotationNode) nodes[0]
        if (parent instanceof FieldNode) {
            final FieldNode fieldNode = (FieldNode) parent


            // Value expression
            final Expression valueExpr = node.getMember("value")

            ClassNode cNode = parent.getOwner()


            // Activity case
            if (AstUtils.inheritsFromClass(cNode, Activity.class)) {

                cNode.methods.each {methodNode ->

                    if (methodNode.name == 'onCreate') {
                        this.modifyActivityOnCreate(methodNode, fieldNode, valueExpr)
                    } else {

                    }
                }
            }

            // Fragment case
            if (AstUtils.inheritsFromClass(cNode, Fragment.class)) {

                cNode.methods.each {methodNode ->

                    if (methodNode.name == 'onCreateView') {
                        this.modifyFragmentOnCreateView(methodNode, fieldNode, valueExpr)
                    }
                }
            }

        }


    }

    private void modifyFragmentOnCreateView(MethodNode methodNode, FieldNode fieldNode, Expression valueExpression) {

        if (methodNode.code instanceof BlockStatement) {

            BlockStatement blockStatement = methodNode.code as BlockStatement

            int fastSnailFragmentViewDeclarationPosition = AstUtils.findVariableDeclarationPosition(blockStatement, 'fastSnailFragmentView')

            ExpressionStatement expressionStatement = new ExpressionStatement(
                    AstUtils.buildFieldAssignmentExpression(fieldNode,
                            new MethodCallExpression(new VariableExpression('fastSnailFragmentView'), 'findViewById', valueExpression)))
            if (fastSnailFragmentViewDeclarationPosition >= 0) {
                // Declaration found : add field assignment after
                blockStatement.statements.add(fastSnailFragmentViewDeclarationPosition + 1, expressionStatement)
            } else {
                // Declaration not found : add it first
                blockStatement.statements.add(0, expressionStatement)
            }
        }
    }

    private Expression createActivityFindViewByIdAssignment(FieldNode fieldNode, Expression valueExpression) {

        return AstUtils.buildFieldAssignmentExpression(fieldNode, new MethodCallExpression(new VariableExpression('this'), 'findViewById', valueExpression))

    }

    private void modifyActivityOnCreate(MethodNode methodNode, FieldNode fieldNode, Expression valueExpression) {

        if (methodNode.code instanceof BlockStatement) {

            BlockStatement blockStatement = methodNode.code as BlockStatement

            int superCallPosition = AstUtils.findMethodCallPosition(blockStatement, 'super', 'onCreate')
            int setContentViewPosition = AstUtils.findMethodCallPosition(blockStatement, 'this', 'setContentView')

            int injectionPosition = (setContentViewPosition > superCallPosition ? setContentViewPosition : superCallPosition) + 1

            blockStatement.statements.add(injectionPosition, new ExpressionStatement(this.createActivityFindViewByIdAssignment(fieldNode, valueExpression)))

        }

    }


}