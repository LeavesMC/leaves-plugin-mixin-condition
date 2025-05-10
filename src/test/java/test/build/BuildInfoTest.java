package test.build;

import org.junit.jupiter.api.Test;
import org.leavesmc.plugin.mixin.condition.build.BuildInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.leavesmc.plugin.mixin.condition.data.MinecraftVersionData.minecraftVersion;

public class BuildInfoTest {
    @Test
    public void testConstructor() {
        BuildInfo bi1 = new BuildInfo(
            "leaves",
            minecraftVersion("1.21.5"),
            114514
        );
        BuildInfo bi2 = BuildInfo.fromString("leaves\t1.21.5\t114514");
        assertEquals(bi1, bi2);
    }

    @Test
    public void testFromStringThrows() {
        assertThrows(NumberFormatException.class, () -> BuildInfo.fromString("leaves\t1.21.5\taaa"));
        assertThrows(IllegalArgumentException.class, () -> BuildInfo.fromString("leaves\t1.21.5"));
        assertThrows(IllegalArgumentException.class, () -> BuildInfo.fromString("leaves"));
    }
}
