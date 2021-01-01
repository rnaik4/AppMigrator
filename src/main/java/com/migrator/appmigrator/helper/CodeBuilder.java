package com.migrator.appmigrator.helper;

import com.migrator.appmigrator.util.FileUtil;
import com.sun.codemodel.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.Map;

public class CodeBuilder {

    public static void generateSpringBootEntryPoint() throws Exception {
        // Instantiate a new JCodeModel
        JCodeModel codeModel = new JCodeModel();

        // Create a new package
        JPackage jp = codeModel._package("com.hack");

        String mainClassName = "SpringBootWebApplication";
        // Create a new class
        JDefinedClass jc = jp._class(mainClassName);
        jc.annotate(SpringBootApplication.class);

        // Implement Serializable
        jc._extends(SpringBootServletInitializer.class);

        // Add Javadoc
        jc.javadoc().add("A JCodeModel example.");

        JMethod mainMethod = jc.method(JMod.PUBLIC | JMod.STATIC, void.class, "main");
        mainMethod.param(String [].class, "args");

        // Add default constructor
        //jc.constructor(JMod.PUBLIC).javadoc().add("Creates a new " + jc.name() + ".");

        // Add constant serializable id
        /*jc.field(JMod.STATIC | JMod.FINAL, Long.class, "serialVersionUID", JExpr.lit(1L));

        // Add private variable
        JFieldVar quantity = jc.field(JMod.PRIVATE, Integer.class, "quantity");

        // Add get method
        JMethod getter = jc.method(JMod.PUBLIC, quantity.type(), "getQuantity");
        getter.body()._return(quantity);
        getter.javadoc().add("Returns the quantity.");
        getter.javadoc().addReturn().add(quantity.name());

        // Add set method
        JMethod setter = jc.method(JMod.PUBLIC, codeModel.VOID, "setQuantity");
        setter.param(quantity.type(), quantity.name());
        setter.body().assign(JExpr._this().ref(quantity.name()), JExpr.ref(quantity.name()));
        setter.javadoc().add("Set the quantity.");
        setter.javadoc().addParam(quantity.name()).add("the new quantity");*/

        // Generate the code
        File file = new File("src/generatedfiles/");
        codeModel.build(file);
        //file.renameTo(new File(mainClassName+".txt"));
    }

    public static void generateController(String name) throws Exception {
        /**
         * @Controller
         * public class WelcomeController {
         *
         * 	// inject via application.properties
         *        @Value("${welcome.message:test}")
         *    private String message = "Hello World";
         *
         *    @RequestMapping("/")
         *    public String welcome(Map<String, Object> model) {
         * 		model.put("message", this.message);
         * 		return "welcome";
         *    }
         *
         * }
         */
        JCodeModel codeModel = new JCodeModel();
        // Create a new package
        JPackage jp = codeModel._package("com.hack");

        String mainClassName = name+"Controller";
        // Create a new class
        JDefinedClass jc = jp._class(mainClassName);
        jc.annotate(Controller.class);

        JMethod method = jc.method(JMod.PUBLIC, String.class, name);
        method.annotate(RequestMapping.class).param("value", "/");
        method.param(Map.class, "model");
        JFieldVar returnVal = jc.field(JMod.PRIVATE, String.class, "value", JExpr.lit(name.toLowerCase()));

        method.body()._return(returnVal);

        File file = new File("src/generatedfiles/");
        codeModel.build(file);
    }
}
