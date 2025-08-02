package be.heimdal.praisesalmongod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

public class HeimdalCommandManager {
    private static final CommandDispatcher<ServerCommandSource> dispatcher = new CommandDispatcher<>();

    public static void RegisterCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SalmonInitiationCommand.register(dispatcher));
    }
}
