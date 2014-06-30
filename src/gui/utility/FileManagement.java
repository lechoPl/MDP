package gui.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.tools.JavaCompiler;
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
            
            // Save source in .java file.
            File root = new File("/java");
            File sourceFile = new File(root, "userCode/" + className + ".java");
            sourceFile.getParentFile().mkdirs();
            new FileWriter(sourceFile).append(source).close();

            // Compile source file.
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            OutputStream out = new OutputStream() {
                String msg = "";

                @Override
                public void write(int i) throws IOException {
                    msg += (char) i;
                }

                @Override
                public String toString() {
                    return msg;
                }
            };

            compiler.run(null, null, out, sourceFile.getPath());
            if (!out.toString().isEmpty()) {
                MyLogger.append(fileName + " : Compiler error\n" + out.toString());
                return null;
            }

            // Load and instantiate compiled class.
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
            Class<?> cls = Class.forName("userCode." + className, true, classLoader);
            IPolicy instance = (IPolicy) cls.newInstance();
            MyLogger.append("Policy " + fileName + " loaded");

            return instance;
        } catch (Exception ex) {
            String msg = "When load policy " + fileName + " load:\n";
            msg += ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);

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

            // Save source in .java file.
            File root = new File("/java");
            File sourceFile = new File(root, "userCode/" + className + ".java");
            sourceFile.getParentFile().mkdirs();
            new FileWriter(sourceFile).append(source).close();

            // Compile source file.
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, sourceFile.getPath());

            OutputStream out = new OutputStream() {
                String msg = "";

                @Override
                public void write(int i) throws IOException {
                    msg += (char) i;
                }

                @Override
                public String toString() {
                    return msg;
                }
            };
            compiler.run(null, null, out, sourceFile.getPath());
            if (!out.toString().isEmpty()) {
                MyLogger.append(fileName + " : Compiler error\n" + out.toString());
                return null;
            }

            // Load and instantiate compiled class.
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
            Class<?> cls = Class.forName("userCode." + className, true, classLoader);
            IAgent instance = (IAgent) cls.newInstance();
            MyLogger.append("Agent " + fileName + " loaded");

            return instance;
        } catch (Exception ex) {
            String msg = "When load agent " + fileName + " load:\n";
            msg += ex.toString() + "\n";
            for (StackTraceElement e : ex.getStackTrace()) {
                msg += "\t" + e.toString() + "\n";
            }
            MyLogger.append(msg);
            return null;
        }
    }

}
