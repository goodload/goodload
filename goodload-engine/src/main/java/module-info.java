module org.divsgaur.goodload.engine {
    requires spring.context;
    requires static lombok;
    requires org.slf4j;
    requires spring.boot;
    requires commons.cli;
    requires org.divsgaur.goodload.dsl;
    requires org.apache.commons.lang3;
    requires java.annotation;
    requires com.fasterxml.jackson.annotation;
    requires spring.boot.autoconfigure;
    requires spring.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    exports org.divsgaur.goodload;
}