package dev.grcq.nitrolib.core.processor;

import com.google.auto.service.AutoService;
import dev.grcq.nitrolib.core.annotations.Timing;
import dev.grcq.nitrolib.core.bytecode.TimingBytecode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Set;

//@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.grcq.nitrolib.core.annotations.Timing")
public class TimingProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(Timing.class)) {
            if (!(element.getEnclosingElement() instanceof TypeElement)) continue;
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            String className = typeElement.getQualifiedName().toString();
            String methodName = element.getSimpleName().toString();
            System.out.println("Processing " + className);

            try {
                FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", className.replace(".", "/") + ".class");
                String output = fileObject.getName();
                ClassReader classReader = new ClassReader(fileObject.openInputStream());
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                TimingBytecode timingBytecode = new TimingBytecode(classWriter);
                classReader.accept(timingBytecode, ClassReader.EXPAND_FRAMES);
                byte[] bytes = classWriter.toByteArray();
                System.out.println("Writing to " + output);
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        File file = new File(output);
                        Files.write(file.toPath(), bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
