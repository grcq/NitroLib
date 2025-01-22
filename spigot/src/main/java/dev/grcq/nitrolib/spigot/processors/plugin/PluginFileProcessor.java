package dev.grcq.nitrolib.spigot.processors.plugin;

import com.google.auto.service.AutoService;
import com.google.inject.internal.util.Sets;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@AutoService(Processor.class)
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

            generatePluginFile(typeElement, typeElement.getQualifiedName().toString(), plugin);
        }

        return false;
    }

    private void generatePluginFile(TypeElement typeElement, String main, Plugin plugin) {
        String pluginYaml = buildPluginYaml(main, plugin);

        try {
            FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            try (Writer writer = fileObject.openWriter()) {
                writer.write(pluginYaml);
            }

        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to generate plugin.yml for " + typeElement.getSimpleName());
        }
    }

    private String buildPluginYaml(String main, Plugin plugin) {
        StringBuilder builder = new StringBuilder();
        builder.append("name: ").append(plugin.name()).append("\n");
        builder.append("version: ").append(plugin.version()).append("\n");
        builder.append("main: ").append(main).append("\n");

        Set<String> softDepend = new HashSet<>(Arrays.asList(plugin.softDepend()));
        softDepend.add("ProtocolLib");

        if (!plugin.description().isEmpty()) builder.append("description: ").append(plugin.description()).append("\n");
        if (plugin.authors().length > 0) {
            if (plugin.authors().length == 1) {
                builder.append("author: ").append(plugin.authors()[0]).append("\n");
            } else {
                builder.append("authors: [").append(String.join(", ", plugin.authors())).append("]\n");
            }
        }
        if (!plugin.website().isEmpty()) builder.append("website: ").append(plugin.website()).append("\n");
        if (plugin.loadBefore().length > 0) builder.append("loadbefore: [").append(String.join(", ", plugin.loadBefore())).append("]\n");
        if (plugin.depend().length > 0) builder.append("depend: [").append(String.join(", ", plugin.depend())).append("]\n");
        builder.append("softdepend: [").append(String.join(", ", softDepend)).append("]\n");
        if (!plugin.prefix().isEmpty()) builder.append("prefix: ").append(plugin.prefix()).append("\n");
        if (!plugin.apiVersion().isEmpty()) builder.append("api-version: ").append(plugin.apiVersion()).append("\n");

        builder.append("load: ").append(plugin.load()).append("\n");
        return builder.toString();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
