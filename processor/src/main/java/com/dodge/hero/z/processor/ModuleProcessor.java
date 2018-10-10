package com.dodge.hero.z.processor;

import com.dodge.hero.z.annotation.ModuleSpec;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by linzheng on 2018/10/10.
 */

@AutoService(Processor.class)
public class ModuleProcessor extends AbstractProcessor {

    private Filer filerUtils; // 文件写入
    private Elements elementUtils; // 操作Element 的工具类
    private Messager messagerUtils; // Log 日志


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        messagerUtils = processingEnv.getMessager();
        processingEnv.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ModuleSpec.class.getName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //得到所有的注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ModuleSpec.class);

        Set<String> moduleSet = new HashSet<>();
        for (Element element : elements) {
            if (element instanceof TypeElement) {
                String clzName = ((TypeElement) element).getQualifiedName().toString();
                moduleSet.add(clzName);
            }
        }

        for (String name : moduleSet) {
            System.out.println(name);
        }
        ClassName setClz = ClassName.get(Set.class);
        ClassName strClz = ClassName.get(String.class);
        TypeName strSetType = ParameterizedTypeName.get(setClz, strClz);


        FieldSpec fieldSpec = FieldSpec.builder(strSetType, "moduleSet", Modifier.PRIVATE, Modifier.STATIC)
                .initializer("new $T<$T>()", HashSet.class, String.class)
                .build();



        MethodSpec methodInit = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addStatement("$T.out.println($S)", System.class, "helloWorld")//定义方法体
                .build();


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
                .addField(fieldSpec)
                .addMethod(methodMain)//添加方法
                .addMethod(methodInit)
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
