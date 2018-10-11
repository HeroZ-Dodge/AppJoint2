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
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by linzheng on 2018/10/11.
 */

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.dodge.hero.z.annotation.RouterSpec", "com.dodge.hero.z.annotation.ModuleSpec"})
public class AppJointProcessor extends AbstractProcessor {


    private Filer filerUtils; // 文件写入
    private Elements elementUtils; // 操作Element 的工具类
    private Messager messagerUtils; // Log 日志
    private Types mTypes;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filerUtils = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        messagerUtils = processingEnv.getMessager();
        mTypes = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        ClassName clzName = ClassName.get(Class.class);
        ClassName mapName = ClassName.get(Map.class);
        ClassName setName = ClassName.get(Set.class);

        Set<String> moduleSet = getModuleSet(roundEnvironment);// 所有Modules
        TypeName moduleSetType = ParameterizedTypeName.get(setName, clzName);
        MethodSpec.Builder moduleSetMethod = MethodSpec.methodBuilder("getModuleSet")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .returns(moduleSetType);
        moduleSetMethod.addStatement("$T<$T> moduleSet = new $T<>()", Set.class, Class.class, HashSet.class);
        for (String name : moduleSet) {
            moduleSetMethod.addStatement("moduleSet.add($T.class)", elementUtils.getTypeElement(name));
        }
        moduleSetMethod.addStatement("return moduleSet");

        Map<String, String> routerMap = getRouterMap(roundEnvironment); // 所有路由
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

        //  生成代码
        TypeSpec typeSpec = TypeSpec.classBuilder("AppJointProvider")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get("com.dodge.hero.z.library", "IAppJointProvider"))
                .addMethod(moduleSetMethod.build())
                .addMethod(routerMapMethod.build())
                .build();
        JavaFile javaFile = JavaFile.builder("com.dodge.hero.z.processor", typeSpec).build();

        try {
            javaFile.writeTo(filerUtils);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 获取路由接口和实现类的对应关系
     * @param roundEnvironment
     */
    private Map<String, String> getRouterMap(RoundEnvironment roundEnvironment) {
        Map<String, String> routerMap = new HashMap<>();
        Set<? extends Element> routerElements = roundEnvironment.getElementsAnnotatedWith(RouterSpec.class);
        for (Element element : routerElements) {
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
     * @param roundEnvironment
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
