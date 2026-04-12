package cn.howxu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import dev.doctor4t.wathe.Wathe;
import eu.midnightdust.lib.config.MidnightConfig;

public class WatheServerConfig extends MidnightConfig {
    // room positions for 10

    @Entry
    public static boolean enableRoomTeleport = true;

    @Entry
    public static List<String> RoomPosition = Lists.newArrayList(
            "1:0.0,0.0,0.0,0",
                        "2:0.0,0.0,0.0,0",
                        "3:0.0,0.0,0.0,0",
                        "4:0.0,0.0,0.0,0",
                        "5:0.0,0.0,0.0,0",
                        "6:0.0,0.0,0.0,0",
                        "7:0.0,0.0,0.0,0"
                        );
    
    // 2. 内存中的快速索引 Map（不标记 @Entry，因为它不需要保存）
    public static final Map<Integer, PresetData> RoomPositions = new HashMap<>();

    @Override
    public void writeChanges(String modid) {
        super.writeChanges(modid);
        rebuildCache();
    }
    /**
     * 将字符串列表解析为高效的内存对象
     */
    public static void rebuildCache() {
        RoomPositions.clear();
        for (String entry : RoomPosition) {
            try {
                String[] parts = entry.split(":");
                int id = Integer.parseInt(parts[0].trim());
                String[] values = parts[1].split(",");
                RoomPositions.put(id, new PresetData(Float.parseFloat(values[0].trim()), 
                        Float.parseFloat(values[1].trim()), Float.parseFloat(values[2].trim()), 
                        Integer.parseInt(values[3].trim())));
            } catch (Exception e) {
                Wathe.LOGGER.info("Room position justify failed!");
            }
        }
    }
    public record PresetData(float f1, float f2, float f3, int i1) {
    }
}
