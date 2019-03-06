package com.dodge.hero.z.processor;

import com.dodge.hero.z.annotation.ModuleSpec;
import com.dodge.hero.z.annotation.RouterSpec;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by linzheng on 2018/10/11.
 */

@AutoService(Processor.class)
@SupportedOptions({"APP_JOINT_MODULE_NAME"})
@SupportedAnnotationTypes({"com.dodge.hero.z.annotation.RouterSpec", "com.dodge.hero.z.annotation.ModuleSpec"})
public class AppJointProcessor extends AbstractProcessor {

    private static final String FACADE_PACKAGE = "com.dodge.hero.z.processor";
    private static final String JAVA_NAME = "AppJointProvider$";
    private static final String OPTIONS_MODULE_NAME = "APP_JOINT_MODULE_NAME";

    private Filer filerUtils; // 文件写入
    private Elements elementUtils; // 操作Element 的工具类
    private Messager mMessagerUtils; // Log 日志
    private Types mTypes;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        mMessagerUtils = processingEnv.getMessager();
        mTypes = processingEnv.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, roundEnvironment.toString());     //打印传入的roundEvn对象信息
        mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, set.toString());                  //遍历annotation，打印出注解类型

        if (set.isEmpty() || roundEnvironment.getRootElements().isEmpty()) {
            mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, "input is empty, return");
            return true;
        }

        ClassName clzName = ClassName.get(Class.class);
        ClassName mapName = ClassName.get(Map.class);
        ClassName setName = ClassName.get(Set.class);
        // getModuleSet()
        Set<String> moduleSet = getModuleSet(roundEnvironment);// 所有Modules
        mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, "module = " + moduleSet.toString());
        TypeName moduleSetType = ParameterizedTypeName.get(setName, clzName);
        MethodSpec.Builder moduleSetMethod = MethodSpec.methodBuilder("getModuleSet")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(moduleSetType);
        moduleSetMethod.addStatement("$T<$T> moduleSet = new $T<>()", Set.class, Class.class, HashSet.class);
        for (String name : moduleSet) {
            moduleSetMethod.addStatement("moduleSet.add($T.class)", elementUtils.getTypeElement(name));
        }
        moduleSetMethod.addStatement("return moduleSet");
        // getRouterMap()
        Map<String, String> routerMap = getRouterMap(roundEnvironment); // 所有路由
        mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, "routerMap = " + routerMap.toString());
        TypeName routerMapType = ParameterizedTypeName.get(mapName, clzName, clzName);
        MethodSpec.Builder routerMapMethod = MethodSpec.methodBuilder("getRouterMap")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(routerMapType);
        routerMapMethod.addStatement("$T<$T, $T> routerMap = new $T<>()", Map.class, Class.class, Class.class, HashMap.class);
        for (Map.Entry<String, String> entry : routerMap.entrySet()) {
            TypeElement interfaceElement = elementUtils.getTypeElement(entry.getKey());
            TypeElement implElement = elementUtils.getTypeElement(entry.getValue());
            routerMapMethod.addStatement("routerMap.put($T.class, $T.class)", interfaceElement, implElement);
        }
        routerMapMethod.addStatement("return routerMap");

        Map<String, String> options = processingEnv.getOptions();
        String moduleName = options.get(OPTIONS_MODULE_NAME);
        //  生成代码
        TypeSpec typeSpec = TypeSpec.classBuilder(JAVA_NAME + moduleName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("com.dodge.hero.z.library", "IAppJointProvider"))
                .addMethod(moduleSetMethod.build())
                .addMethod(routerMapMethod.build())
                .build();
        JavaFile javaFile = JavaFile.builder(FACADE_PACKAGE, typeSpec).build();

        try {
            javaFile.writeTo(filerUtils);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取路由接口和实现类的对应关系
     *
     * @param roundEnvironment roundEnvironment
     */
    private Map<String, String> getRouterMap(RoundEnvironment roundEnvironment) {
        Map<String, String> routerMap = new HashMap<>();
        Set<? extends Element> routerElements = roundEnvironment.getElementsAnnotatedWith(RouterSpec.class);
        for (Element element : routerElements) {
            // 读取自定义注解的值
            String value = element.getAnnotation(RouterSpec.class).value();
            mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, "path = " + value);
            if (element instanceof TypeElement && element.getKind() == ElementKind.CLASS) {
                String className = ((TypeElement) element).getQualifiedName().toString();
                List<? extends TypeMirror> typeMirrors = ((TypeElement) element).getInterfaces();
                for (TypeMirror typeMirror : typeMirrors) {
                    Element e = mTypes.asElement(typeMirror);
                    if (e instanceof TypeElement) {
                        String interfaceName = ((TypeElement) e).getQualifiedName().toString();
                        routerMap.put(interfaceName, className);
                    }
                }
            }
        }
        return routerMap;
    }

    /**
     * 获取各模块信息
     *
     * @param roundEnvironment roundEnvironment
     */
    private Set<String> getModuleSet(RoundEnvironment roundEnvironment) {
        Set<String> moduleSet = new HashSet<>();
        Set<? extends Element> moduleElements = roundEnvironment.getElementsAnnotatedWith(ModuleSpec.class);
        for (Element element : moduleElements) {
            if (element instanceof TypeElement && element.getKind() == ElementKind.CLASS) {
                moduleSet.add(((TypeElement) element).getQualifiedName().toString());
            }
        }
        return moduleSet;
    }


}
