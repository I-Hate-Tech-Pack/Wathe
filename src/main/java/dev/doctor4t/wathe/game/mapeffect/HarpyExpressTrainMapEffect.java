package dev.doctor4t.wathe.game.mapeffect;

import dev.doctor4t.wathe.api.MapEffect;
import dev.doctor4t.wathe.api.WatheMapEffects;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.TrainWorldComponent;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.index.WatheItems;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;

import cn.howxu.WatheServerConfig;

public abstract class HarpyExpressTrainMapEffect extends MapEffect {
    public HarpyExpressTrainMapEffect(Identifier identifier) {
        super(identifier);
    }

    @Override
    public void initializeMapEffects(ServerWorld serverWorld, List<ServerPlayerEntity> players) {
        TrainWorldComponent trainWorldComponent = TrainWorldComponent.KEY.get(serverWorld);
        trainWorldComponent.setSnow(true);
        trainWorldComponent.setFog(true);
        trainWorldComponent.setHud(true);
        trainWorldComponent.setSpeed(130);
        trainWorldComponent.setTime(0);

        // select rooms
        Collections.shuffle(players);
        int roomNumber = 0;
        for (ServerPlayerEntity serverPlayerEntity : players) {
            ItemStack itemStack = new ItemStack(WatheItems.KEY);
            roomNumber = roomNumber % 10 + 1;
            int finalRoomNumber = roomNumber;
            itemStack.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, component -> new LoreComponent(Text.literal("Room " + finalRoomNumber).getWithStyle(Style.EMPTY.withItalic(false).withColor(0xFF8C00))));
            serverPlayerEntity.giveItemStack(itemStack);
            // This judege the room number and the player
            // Just Put serverPlayerEntity in the Map We need
            var roomData = WatheServerConfig.RoomPositions.get(roomNumber);
            // 简单的空值判定 0 means disable config control
            if (WatheServerConfig.enableRoomTeleport && roomData != null && roomData.i1() != 0) {
                // plus one for block location
                // teleport
                serverPlayerEntity.teleport(
                        (ServerWorld) serverPlayerEntity.getWorld(),
                        roomData.f1() + 0.5,
                        roomData.f2() + 0.5,
                        roomData.f3() + 0.5,
                        serverPlayerEntity.getYaw(),
                        serverPlayerEntity.getPitch());
            }

            // give letter
            ItemStack letter = new ItemStack(WatheItems.LETTER);

            letter.set(DataComponentTypes.ITEM_NAME, Text.translatable(letter.getTranslationKey()));
            int letterColor = 0xC5AE8B;
            String tipString = "tip.letter.";
            letter.apply(DataComponentTypes.LORE, LoreComponent.DEFAULT, component -> {
                        List<Text> text = new ArrayList<>();
                        UnaryOperator<Style> stylizer = style -> style.withItalic(false).withColor(letterColor);

                        Text displayName = serverPlayerEntity.getDisplayName();
                        String string = displayName != null ? displayName.getString() : serverPlayerEntity.getName().getString();
                        if (string.charAt(string.length() - 1) == '\uE780') { // remove ratty supporter icon
                            string = string.substring(0, string.length() - 1);
                        }

                        text.add(Text.translatable(tipString + "name", string).styled(style -> style.withItalic(false).withColor(0xFFFFFF)));
                        text.add(Text.translatable(tipString + "room").styled(stylizer));
                        text.add(Text.translatable(tipString + "tooltip1",
                                Text.translatable(tipString + "room." + switch (finalRoomNumber) {
                                    case 1 -> "grand_suite";
                                    case 2, 3 -> "cabin_suite";
                                    default -> "twin_cabin";
                                }).getString()
                        ).styled(stylizer));
                        text.add(Text.translatable(tipString + "tooltip2").styled(stylizer));

                        return new LoreComponent(text);
                    }
            );
            serverPlayerEntity.giveItemStack(letter);
        }
    }

    @Override
    public void finalizeMapEffects(ServerWorld serverWorld, List<ServerPlayerEntity> players) {
        // switch back to the lobby map effects
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(serverWorld);
        gameWorldComponent.setMapEffect(WatheMapEffects.HARPY_EXPRESS_LOBBY);
        gameWorldComponent.getMapEffect().initializeMapEffects(serverWorld, players);

    }
}
