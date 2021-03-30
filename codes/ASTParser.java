package controller.ballerina.parser;

import controller.ballerina.ast.ActionInvocationFinderVisitor;
import org.apache.log4j.Logger;
import org.ballerinalang.compiler.CompilerPhase;
import org.ballerinalang.toml.exceptions.TomlException;
import org.ballerinalang.toml.model.Manifest;
import org.ballerinalang.toml.parser.ManifestProcessor;
import org.springframework.stereotype.Service;
import org.wso2.ballerinalang.compiler.Compiler;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;
import org.wso2.ballerinalang.compiler.util.CompilerOptions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;



import static org.ballerinalang.compiler.CompilerOptionName.COMPILER_PHASE;


import static org.ballerinalang.compiler.CompilerOptionName.EXPERIMENTAL_FEATURES_ENABLED;


import static org.ballerinalang.compiler.CompilerOptionName.LOCK_ENABLED;


import static org.ballerinalang.compiler.CompilerOptionName.OFFLINE;


import static org.ballerinalang.compiler.CompilerOptionName.PRESERVE_WHITESPACE;


import static org.ballerinalang.compiler.CompilerOptionName.PROJECT_DIR;


import static org.ballerinalang.compiler.CompilerOptionName.SKIP_TESTS;


import static org.ballerinalang.compiler.CompilerOptionName.TEST_ENABLED;

/**
* parser to extract action invocations with necessary info.
*/


@Service
public class ActionInvocationParser {


   private static final Logger logger = Logger.getLogger(ActionInvocationParser.class);


   private EmptyPrintStream emptyPrintStream;


   public ActionInvocationParser() {


   }



   static Manifest getManifest(Path projectDirPath) {

       Path tomlFilePath = projectDirPath.resolve("Ballerina.toml");

       try {

           return ManifestProcessor.parseTomlContentFromFile(tomlFilePath);


       } catch (TomlException | IOException var3) {

           return new Manifest();

       }


   }


   public synchronized HashMap<String, Object> getServiceNameList(String sourceRoot) {



       long startTime = System.currentTimeMillis();

       BLangPackage modules = getBLangPackage(sourceRoot);

       ActionInvocationFinderVisitor visitor = new ActionInvocationFinderVisitor();

       modules.accept(visitor);

       logger.debug("time elapsed " + (System.currentTimeMillis() - startTime) + "ms");

       return visitor.getActionInvocations();


   }


   public BLangPackage getBLangPackage(String sourceRoot) {


       BLangPackage result = null;


       try {


           String[] pathArray = sourceRoot.split("/"); //sourceRoot can be for example '/home/foo/proj1/src/mod1'


           StringBuilder srcRoot = new StringBuilder();


           int counter = pathArray.length - 2; //here we drop 'src' and 'mod1' from '/home/foo/proj1/src/mod1'


           for (int i = 1; i < counter; i++) {


               srcRoot.append("/");


               srcRoot.append(pathArray[i]);


           }


           sourceRoot = srcRoot.toString();


           emptyPrintStream = new EmptyPrintStream();


           CompilerContext context = getCompilerContext(Paths.get(sourceRoot));

           Compiler compiler = Compiler.getInstance(context);

           compiler.setOutStream(emptyPrintStream);

           result = compileModule(Paths.get(sourceRoot), pathArray[pathArray.length - 1]);


       } catch (UnsupportedEncodingException e) {


           logger.error("Error in compiling module", e);


       }

       return result;


   }

   /**
    * Get prepared compiler context.
    *
    * @param sourceRootPath ballerina compilable source root path
    * @return {@link CompilerContext} compiler context
    */


   private CompilerContext getCompilerContext(Path sourceRootPath) {


       CompilerPhase compilerPhase = CompilerPhase.COMPILER_PLUGIN;
       CompilerContext context = new CompilerContext();
       CompilerOptions options = CompilerOptions.getInstance(context);
       options.put(PROJECT_DIR, sourceRootPath.toString());
       options.put(OFFLINE, Boolean.toString(false));
       options.put(COMPILER_PHASE, compilerPhase.toString());
       options.put(SKIP_TESTS, Boolean.toString(false));
       options.put(TEST_ENABLED, "true");
       options.put(LOCK_ENABLED, Boolean.toString(false));
       options.put(EXPERIMENTAL_FEATURES_ENABLED, Boolean.toString(true));
       options.put(PRESERVE_WHITESPACE, Boolean.toString(true));

       return context;

   }


   /**
    * Compile only a ballerina module.
    *
    * @param sourceRoot source root
    * @param moduleName name of the module to be compiled
    * @return {@link BLangPackage} ballerina package
    */


   private BLangPackage compileModule(Path sourceRoot, String moduleName) throws UnsupportedEncodingException {


       emptyPrintStream = new EmptyPrintStream();
       CompilerContext context = getCompilerContext(sourceRoot);
       Compiler compiler = Compiler.getInstance(context);

       // Set an EmptyPrintStream to hide unnecessary outputs from compiler.
       compiler.setOutStream(emptyPrintStream);
       return compiler.compile(moduleName);


   }


   /**
    * Empty print stream extending the print stream.
    */

   static class EmptyPrintStream extends PrintStream {


       EmptyPrintStream() throws UnsupportedEncodingException {

           super(new OutputStream() {

               @Override
               public void write(int b) {


               }

           }, true, "UTF-8");
       }

   }

}
