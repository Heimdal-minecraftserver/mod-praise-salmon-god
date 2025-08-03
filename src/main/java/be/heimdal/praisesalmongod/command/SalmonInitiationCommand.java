package be.heimdal.praisesalmongod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SalmonInitiationCommand {
    private static final String commandName = "salmoninitiation";
    private static final Map<UUID, Integer> tickCounters = new HashMap<>();
    private static final int DURATION_TICKS = 20 * 10; // 5 seconds
    private static boolean tickHandlerRegistered = false;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal(commandName)
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
        UUID uuid = target.getUuid();

        if(tickCounters.containsKey(uuid)){
            source.sendFeedback(() -> Text.literal(target.getName().getString())
                    .append(" is already being salmoned."), false);
        }
        source.sendFeedback(() -> Text.literal("Let the salmon initiation for ")
                .append(target.getDisplayName())
                .append(" begin!"), true);

        tickCounters.put(uuid, 0);

        if(!tickHandlerRegistered) {
            ServerTickEvents.END_SERVER_TICK.register(server ->
                    tickCounters.entrySet().removeIf(entry ->{
                UUID playerId = entry.getKey();
                int tick = entry.getValue();

                ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
                if(player == null) return true; // Player left, clean up
                tick++;

                if(tick % 10 == 0) {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    double offsetX = random.nextDouble(-1, 1);
                    double offsetZ = random.nextDouble(-1, 1);
                    double spawnX = player.getX() + offsetX;
                    double spawnY = player.getY() + 5.0;
                    double spawnZ = player.getZ() + offsetZ;

                    // Spawn particles from which they fall
                    player.getWorld().spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            player.getX(),
                            spawnY,
                            player.getZ(),
                            4,
                            0.5,0.2,0.5,
                            0.0);
                    player.getWorld().spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                            player.getX(),
                            spawnY - 0.5f,
                            player.getZ(),
                            2,
                            0.3,0.2,0.3,
                            0.0);

                    SalmonEntity fish = new SalmonEntity(EntityType.SALMON, player.getWorld());
                    fish.refreshPositionAndAngles(spawnX, spawnY, spawnZ, 0, 0);
                    // Duration 20 seconds
                    fish.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 50, 4));
                    fish.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200, 0));
                    player.getWorld().spawnEntity(fish);
                }

                if (tick >= DURATION_TICKS) {
                    return true;
                } else {
                    entry.setValue(tick);
                    return false;
                }
            }));
            tickHandlerRegistered = true;
        }
        return 1;
    }
}
