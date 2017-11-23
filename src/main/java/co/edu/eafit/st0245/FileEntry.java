package co.edu.eafit.st0245;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

public @Data
@RequiredArgsConstructor
class FileEntry {
    private @NonNull String owner;
    private @NonNull String size;
    private @NonNull String name;
    private @NonNull String fullPath;
    private final int level;

    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.NONE)
    private Set<FileEntry> files = new HashSet<>();

    @Override
    public String toString() {
        return getFullPath();
    }
}
