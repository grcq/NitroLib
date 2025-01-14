package dev.grcq.nitrolib.spigot.command.parser.data;

import java.lang.annotation.Annotation;

public interface IData {

    Class<? extends Annotation> forType();

}
