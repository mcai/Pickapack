package net.pickapack.apk;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.environment.EnvironmentUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ApkHelper {
    public static void decode(String fileNameApk, String folderNameOut) throws IOException {
        Executor exec = new DefaultExecutor();

        exec.setWorkingDirectory(new File("/home/itecgo/Business/PickaPack/projects/PickaPack/tools/apk"));

        CommandLine cl = new CommandLine("sh");
        cl.addArguments("-x ./decode.sh " + fileNameApk + " " + folderNameOut);

        exec.execute(cl);
    }

    public static void build(String folderNameOut, String fileNameApk) throws IOException {
        Executor exec = new DefaultExecutor();

        exec.setWorkingDirectory(new File("/home/itecgo/Business/PickaPack/projects/PickaPack/tools/apk"));

        CommandLine cl = new CommandLine("sh");
        cl.addArguments("-x ./build.sh " + folderNameOut + " " + fileNameApk);

        exec.execute(cl);
    }

    public static void sign(String fileNameApkIn, String fileNameApkOut) throws IOException {
        Map environment = EnvironmentUtils.getProcEnvironment();

        Executor exec = new DefaultExecutor();

        exec.setWorkingDirectory(new File("/home/itecgo/Business/PickaPack/projects/PickaPack/tools/apk"));

        CommandLine cl = new CommandLine("sh");
        cl.addArguments("-x ./sign.sh " + fileNameApkIn + " " + fileNameApkOut);

        exec.execute(cl, environment);
    }

    public static void main(String[] args) throws IOException {
        decode("/home/itecgo/Business/PickaPack/projects/PickaPack/tools/apk/Amazon_Appstore-release.apk", "app");
        build("app", "/home/itecgo/Desktop/modified_Amazon_Appstore-release.apk");
        sign("/home/itecgo/Desktop/modified_Amazon_Appstore-release.apk", "/home/itecgo/Desktop/signed_modified_Amazon_Appstore-release.apk");
    }
}
