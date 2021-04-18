module org.divsgaur.goodload.http {
    exports org.divsgaur.goodload.http;
    requires static lombok;
    requires org.slf4j;
    requires spring.context;
    requires spring.beans;
    requires okhttp;
    requires java.annotation;
    requires org.divsgaur.goodload.dsl;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
}