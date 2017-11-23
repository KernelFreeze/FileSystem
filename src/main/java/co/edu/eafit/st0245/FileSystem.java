package co.edu.eafit.st0245;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystem {
    private static Logger log = Logger.getLogger("FileSystem");
    private static Stack<FileEntry> lastPath = new Stack<>();

    public static void main(String[] args) {
        if (args.length <= 1) {
            log.warning("Usage: java -jar FileSystem.jar <input_file> <file_search>");
            return;
        }

        File f = new File(args[0]);

        if (!f.exists() || f.isDirectory()) {
            log.warning("Bad file input");
            return;
        }

        BPlusTree<String, FileEntry> tree = new BPlusTree<>();

        FileEntry root = new FileEntry("root", "0", "/", "/", 0);
        lastPath.push(root);
        tree.insert("/", root);

        // Regex, donde el primer grupo es el owner, el segundo tamaño y tercero nombre
        Pattern p = Pattern.compile("\\[(\\w*) *([\\d\\.MKGT]*)\\] *(.*)$", Pattern.CASE_INSENSITIVE);

        try (Stream<String> stream = Files.lines(Paths.get(args[0]))) {
            stream.forEach(x -> {
                int level = x.split("\\[")[0].length();
                Matcher m = p.matcher(x);

                if (!m.find()) return;

                String name = m.group(3);
                StringBuilder path = new StringBuilder();

                FileEntry parent;

                do {
                    parent = lastPath.pop();
                } while (parent.getLevel() >= level && !lastPath.empty());

                path.append(parent.getFullPath());
                if (!path.toString().equals("/"))
                    path.append("/");
                path.append(name);

                FileEntry file = new FileEntry(m.group(1), m.group(2), name, path.toString(), level);
                tree.insert(file.getFullPath(), file);

                parent.getFiles().add(file);

                lastPath.push(parent);
                lastPath.push(file);
            });

            // Debug:
            //System.out.println(tree);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileEntry path = tree.search(args[1]);

        if (path == null) {
            log.warning("No such file or directory");
        } else {
            System.out.println(String.join(", ", path.getFiles()
                    .stream()
                    .map(FileEntry::getFullPath)
                    .collect(Collectors.toList())
            ));
        }
    }
}
