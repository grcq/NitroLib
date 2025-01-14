package dev.grcq.nitrolib.spigot.processors.plugin;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes("dev.grcq.nitrolib.spigot.processors.plugin.Plugin")
public class PluginFileProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Plugin.class)) {
            if (!(element instanceof TypeElement)) continue;

            TypeElement typeElement = (TypeElement) element;
            Plugin plugin = typeElement.getAnnotation(Plugin.class);

            generatePluginFile(typeElement, plugin);
        }

        return false;
    }

    private void generatePluginFile(TypeElement typeElement, Plugin plugin) {
        String pluginYaml = buildPluginYaml(plugin);

        try {
            FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            try (Writer writer = fileObject.openWriter()) {
                writer.write(pluginYaml);
            }

        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to generate plugin.yml for " + typeElement.getSimpleName());
        }
    }

    private String buildPluginYaml(Plugin plugin) {
        return null;
    }
}
