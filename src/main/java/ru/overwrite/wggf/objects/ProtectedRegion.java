package ru.overwrite.wggf.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProtectedRegion {

    @Getter
    String world;
    int x1, y1, z1, x2, y2, z2;

    public boolean contains(int x, int y, int z) {
        return (y >= this.y1 && y <= this.y2 && contains(x, z));
    }

    public boolean contains(int x, int z) {
        return (x >= this.x1 && z >= this.z1 && x <= this.x2 && z <= this.z2);
    }
}
