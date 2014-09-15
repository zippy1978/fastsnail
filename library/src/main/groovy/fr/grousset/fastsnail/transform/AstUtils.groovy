package fr.grousset.fastsnail.transform

import android.app.Activity
import android.os.Bundle
import android.view.View
import groovyjarjarasm.asm.Opcodes
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types

/**
 * AST utilities.
 * @author gi.grousset@gmail.com
 */
public class AstUtils {

    /**
     * Find variable declaration position in block statment
     * @param blockStatement Block statment
     * @param variableName Variable name
     * @return Declaration position or -1 if not found
     */
    public static int findVariableDeclarationPosition(BlockStatement blockStatement, String variableName) {

        int position = -1

        int index = 0
        for (Statement statement : blockStatement.statements) {
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement expressionStatement = statement as ExpressionStatement
                if (expressionStatement.expression instanceof DeclarationExpression) {
                    DeclarationExpression declarationExpression = expressionStatement.expression as DeclarationExpression
                    if (declarationExpression.leftExpression instanceof VariableExpression) {
                        VariableExpression variableExpression = declarationExpression.leftExpression as VariableExpression
                        if (variableExpression.text == variableName) {
                            position = index
                            break
                        }
                    }
                }
            }
            index++
        }

        return position
    }

    /** Look for method with a given object and name within a block statement.
     * @param blockStatement Block statement
     * @param object Method call object
     * @param methodName Method name
     * @return Found method position in block, -1 if not found
     */
    public static int findMethodCallPosition(BlockStatement blockStatement, String object, String methodName) {

        int position = -1

        int index = 0
        for (Statement statement : blockStatement.statements) {
            if (statement instanceof ExpressionStatement) {
                ExpressionStatement expressionStatement = statement as ExpressionStatement
                if (expressionStatement.expression instanceof MethodCallExpression) {
                    MethodCallExpression methodCallExpression = expressionStatement.expression as MethodCallExpression
                    if (methodCallExpression.objectExpression.text == object && methodCallExpression.methodAsString == methodName) {
                        position = index
                        break
                    }
                }
            }
            index++
        }

        return position
    }

    /**
     * Look for return statement position in a block statment
     * @param blockStatement Block statement
     * @return Found method position in block, -1 if not found
     */
    public static int findReturnPosition(BlockStatement blockStatement) {

        int position = -1

        int index = 0
        for (Statement statement : blockStatement.statements) {
            if (statement instanceof ReturnStatement) {
                position = index
                break
            }
            index++
        }

        return position
    }

    /**
     * Build a filed assignment expression.
     * @param fieldNode Field to assign
     * @param valueExpression Value to assign
     * @return An Expression
     */
    public static Expression buildFieldAssignmentExpression(FieldNode fieldNode, Expression valueExpression) {

        return new BinaryExpression(
                new VariableExpression(fieldNode),
                Token.newSymbol(Types.EQUAL, 0, 0),
                valueExpression
        )
    }

    /**
     * Check if a ClassNode inherits from a given class.
     * @param classNode ClassNode to test
     * @param clazz Ancestor class
     * @return true/false
     */
    public static boolean inheritsFromClass(ClassNode classNode, Class clazz) {


        ClassNode parent = classNode.unresolvedSuperClass
        while (parent.unresolvedSuperClass) {
            if (parent.text == clazz.name) {
                return true
            }

            parent = parent.unresolvedSuperClass

        }

        return false
    }
}