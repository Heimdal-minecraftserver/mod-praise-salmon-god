package be.heimdal.praisesalmongod.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class SalmonInitiationCommand {
    private static final Map<MinecraftServer, Integer> tickCounters = new HashMap<>();
    private static final int DURATION_TICKS = 20 * 10; // 5 seconds

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("salmoninitiation")
                        .requires(CommandManager.requirePermissionLevel(2))
                        .executes(context -> {
                            Entity sender = context.getSource().getEntityOrThrow();
                            if(!(sender instanceof ServerPlayerEntity player)){
                                context.getSource().sendError(Text.literal("Only players can be salmoned!"));
                                return 0;
                            }

                            return execute(context.getSource(), player);
                        })
                        .then(
                                CommandManager.argument("target", EntityArgumentType.player())
                                        .executes(context -> execute(context.getSource(), EntityArgumentType.getPlayer(context, "target")))
                        )
        );
    }

    private static int execute(ServerCommandSource source, ServerPlayerEntity target) {
        // TODO: Fix issue with salmon duplication
        // TODO: Fix issue with last player being salmed
        source.sendFeedback(() -> Text.literal("Let the salmon initiation for ")
                .append(target.getDisplayName())
                .append(" begin!"), true);

        MinecraftServer server = source.getServer();
        tickCounters.put(server, 0);

        ServerTickEvents.END_SERVER_TICK.register(s -> {
            if(!tickCounters.containsKey(s)) return;

            int tick = tickCounters.get(s);
            tick++;

            if(tick % 10 == 0) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    int offsetX = random.nextInt(-1, 2);
                    int offsetY = random.nextInt(-1, 2);
                    ServerWorld world = (ServerWorld) target.getWorld();
                    BlockPos pos = target.getBlockPos().up(5).add(offsetX, 0, offsetY); // 5 blocks above
                    SalmonEntity fish = new SalmonEntity(EntityType.SALMON, world);
                    fish.refreshPositionAndAngles(pos, 0, 0);
                    world.spawnEntity(fish);
            }

            if (tick >= DURATION_TICKS) {
                tickCounters.remove(s);
            } else {
                tickCounters.put(s, tick);
            }
        });

        return 1;
    }
}
