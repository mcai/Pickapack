package net.pickapack.cracker;

import net.pickapack.action.Predicate;
import net.pickapack.io.file.IterableBigTextFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;

public class PasswordCracker {
    public static CrackResult crack(Predicate<String> passwordPred, int numMaxCrackings, int startIndex) throws FileNotFoundException {
        return crack(passwordPred, numMaxCrackings, startIndex, "/home/itecgo/Tools/crackers/leaked", "/home/itecgo/Tools/crackers/dictionaries");
    }

    public static CrackResult crack(Predicate<String> passwordPred, int numMaxCrackings, int startIndex, String... folders) throws FileNotFoundException {
        System.out.printf("Cracking using password begins at %d until %d crackings%n", startIndex, numMaxCrackings);
        int i = 0;
        int numCrackings = 0;
        for(String folder : folders) {
            Collection<File> files = FileUtils.listFiles(new File(folder), new String[]{"txt"}, true);
            for(File file : files) {
                IterableBigTextFile file1 = new IterableBigTextFile(new FileReader(file));
                for(String str : file1) {
                    i++;
                    if(i > startIndex) {
                        if(passwordPred.apply(str)) {
                            System.out.printf("password '%s' found in %s after %d crackings.%n", str, file.getAbsolutePath(), numCrackings);
                            file1.close();
                            return new CrackResult(true, str, i);
                        }
                        numCrackings++;
                    }

                    if(numMaxCrackings != -1 && numCrackings >= numMaxCrackings) {
                        file1.close();
                        System.out.printf("Password not found: reaching numMaxCrackings threshold.%n");
                        return new CrackResult(false, null, i);
                    }
                }
                file1.close();
            }
        }
        System.out.printf("Password not found: exhausted.%n");
        return new CrackResult(false, null, i);
    }
}
