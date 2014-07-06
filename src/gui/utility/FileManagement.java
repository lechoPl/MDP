package gui.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import userCode.*;
import world.World;

public class FileManagement {

    public static final String agentsPath = "./agents/";
    public static final String agentExtension = ".agent";

    public static final String policyPath = "./policy/";
    public static final String policyExtension = ".policy";

    public static final String worldsPath = "./worlds/";
    public static final String worldExtension = ".world";

    public static ArrayList<String> getAgentsFileName() {
        File folder = new File(agentsPath);

        ArrayList<String> result = new ArrayList<>();

        if (!folder.exists()) {
            folder.mkdir();
        }

        if (folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                String tempName = file.getName();
                if (tempName.endsWith(agentExtension)) {
                    result.add(tempName);
                }
            }
        }
        return result;
    }

    public static ArrayList<String> getPolicyFileName() {
        File folder = new File(policyPath);

        ArrayList<String> result = new ArrayList<>();

        if (!folder.exists()) {
            folder.mkdir();
        }

        if (folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                String tempName = file.getName();
                if (tempName.endsWith(policyExtension)) {
                    result.add(tempName);
                }
            }
        }

        return result;
    }

    public static ArrayList<String> getWorldsFileName() {
        File folder = new File(worldsPath);

        ArrayList<String> result = new ArrayList<>();

        if (!folder.exists()) {
            folder.mkdir();
        }

        if (folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                String tempName = file.getName();
                if (tempName.endsWith(worldExtension)) {
                    result.add(tempName);
                }
            }
        }
        return result;
    }

    public static void SaveWorld(World w, String name) {
        if (w == null) {
            return;
        }

        try {
            if (name == null || name.isEmpty()) {
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
                Date now = new Date();
                name = dateFormat.format(now);
            }

            w.setName(name);
            File folder = new File(worldsPath);
            if (!folder.exists()) {
                folder.mkdir();
            }

            File newTextFile = new File(worldsPath + name + worldExtension);

            FileWriter fw = new FileWriter(newTextFile);
            fw.write(w.toString());
            fw.close();

            MyLogger.append("World " + name + " saved");
        } catch (IOException ex) {
            MyLogger.append(ex.toString());
        }
    }

    public static World loadWorld(String fileName) {
        try {
            World w = World.loadFile(worldsPath, fileName, worldExtension);

            MyLogger.append("World " + w.getName() + " loaded");
            return w;
        } catch (Exception ex) {
            String msg = ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

        }

        return null;
    }

    protected static boolean CompileCode(String className, String fileName, String source)
            throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        File folder = new File("./temp");
        if (!folder.exists()) {
            folder.mkdir();
        }

        boolean status = false;
        File sourceFile = new File("./temp/" + className + ".java");
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector();

        FileWriter writer = new FileWriter(sourceFile);
        try {
            writer.write(source);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File[]{new File("./")}));

            status = compiler.getTask(null, fileManager, diagnostics, null, null, fileManager.getJavaFileObjectsFromFiles(Arrays.asList(new File[]{sourceFile}))).call();
        }
        if (!status) {
            String tempMsg = "";
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                tempMsg = tempMsg + diagnostic + "\n";
            }

            MyLogger.append(tempMsg);
        }
        return status;
    }

    public static boolean checkPolicyCode(String code) {
        if (!code.contains("package userCode;")
                || !code.contains("public class ")
                || !code.contains(" implements IPolicy")) {
            return false;
        }

        return true;
    }

    public static IPolicy loadPolicy(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(policyPath + fileName),
                    Charset.defaultCharset());
            String source = "";
            for (String l : lines) {
                source += l + "\n";
            }

            if (!checkPolicyCode(source)) {
                MyLogger.append(fileName + " is incorrect implementation of IPolicy");

                return null;
            }

            int intedxPC = source.indexOf("public class ") + "public class ".length();
            int intedxIm = source.indexOf(" ", intedxPC);

            String className = source.substring(intedxPC, intedxIm).trim();

            IPolicy instance = null;
            if (CompileCode(className, fileName, source)) {
                MyClassLoader mcl = new MyClassLoader(ClassLoader.getSystemClassLoader());
                instance = (IPolicy) mcl.loadMyClass(className).newInstance();
            }

            MyLogger.append("Policy " + fileName + " loaded");

            return instance;
        } catch (Exception ex) {
            String msg = "When load policy " + fileName + " load:\n";
            msg += ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

            MyLogger.append("Uruchom aplikacj pod JDK zamiast JRE");
            return null;
        }
    }

    public static boolean checkAgentCode(String code) {
        if (!code.contains("package userCode;")
                || !code.contains("public class ")
                || !code.contains(" implements IAgent")) {
            return false;
        }

        return true;
    }

    public static IAgent loadAgent(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(agentsPath + fileName),
                    Charset.defaultCharset());
            String source = "";
            for (String l : lines) {
                source += l + "\n";
            }

            if (!checkAgentCode(source)) {
                MyLogger.append(fileName + " is incorrect implementation of IAgent");

                return null;
            }

            int intedxPC = source.indexOf("public class ") + "public class ".length();
            int intedxIm = source.indexOf(" ", intedxPC);

            String className = source.substring(intedxPC, intedxIm).trim();

            IAgent instance = null;
            if (CompileCode(className, fileName, source)) {
                MyClassLoader mcl = new MyClassLoader(ClassLoader.getSystemClassLoader());
                instance = (IAgent) mcl.loadMyClass(className).newInstance();
            }

            MyLogger.append("Agent " + fileName + " loaded");

            return instance;
        } catch (Exception ex) {
            String msg = "When load agent " + fileName + " load:\n";
            msg += ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

            MyLogger.append("Uruchom aplikacj pod JDK zamiast JRE");
            return null;
        }
    }

}

class MyClassLoader
        extends ClassLoader {

    String _referenceName = "";

    public MyClassLoader(ClassLoader parent) {
        super(parent);
    }

    public String getReferenceName() {
        return this._referenceName;
    }

    public void setReferenceName(String name) {
        this._referenceName = name;
    }

    public Class loadMyClass(String name)
            throws ClassNotFoundException {
        setReferenceName(name);
        return loadClass(name);
    }

    @Override
    public Class loadClass(String name)
            throws ClassNotFoundException {
        if (!this._referenceName.equals(name)) {
            return super.loadClass(name);
        }
        try {
            String url = "file:./userCode/" + name + ".class";
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();

            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer;
            try {
                buffer = new ByteArrayOutputStream();
                int data = input.read();
                while (data != -1) {
                    buffer.write(data);
                    data = input.read();
                }
            } finally {
                input.close();
            }
            byte[] classData = buffer.toByteArray();

            //System.out.println(name);
            return defineClass(null, classData, 0, classData.length);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
