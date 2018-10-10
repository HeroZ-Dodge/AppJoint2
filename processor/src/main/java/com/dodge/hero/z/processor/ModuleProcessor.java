package com.dodge.hero.z.processor;

import com.dodge.hero.z.annotation.ModuleSpec;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by linzheng on 2018/10/10.
 */

@AutoService(Processor.class)
public class ModuleProcessor extends AbstractProcessor {


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ModuleSpec.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //得到所有的注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ModuleSpec.class);

        for (Element element : elements) {
//            VariableElement variableElement = (VariableElement) element;
//            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
//            String fullClassName = classElement.getQualifiedName().toString();
            System.out.println(String.format("className = %s", element.getSimpleName()));
        }


        MethodSpec methodMain = MethodSpec.methodBuilder("main")//创建main方法
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//定义修饰符为 public static
                .addJavadoc("@create by apt")//在生成的代码前添加注释
                .returns(void.class)//定义返回类型
                .addParameter(String[].class, "args")//定义方法参数
                .addStatement("$T.out.println($S)", System.class, "helloWorld")//定义方法体
                .addStatement("$T.out.println($S)", System.class, set.size())//定义方法体
                .build();
        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")   //创建HelloWorld类
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//定义修饰符为 public final
                .addMethod(methodMain)//添加方法
                .addJavadoc("@中文")//定义方法参数
                .build();
        JavaFile javaFile = JavaFile.builder("com.z.hero.dodge", helloWorld).build();// 生成源   代码
        try {
            Filer mFiler = processingEnv.getFiler();//文件相关的辅助类
            javaFile.writeTo(mFiler);//// 在 app module/build/generated/source/apt 生成一份源代码
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
