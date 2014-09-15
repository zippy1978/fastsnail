package fr.grousset.fastsnail.transform

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(["fr.grousset.fastsnail.transform.InjectLayoutASTTransformation"])
public @interface InjectLayout {
    int value()
}