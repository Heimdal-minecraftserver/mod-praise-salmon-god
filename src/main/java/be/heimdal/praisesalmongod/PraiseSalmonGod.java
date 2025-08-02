package be.heimdal.praisesalmongod;

import be.heimdal.praisesalmongod.chat.MessageManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PraiseSalmonGod implements ModInitializer {
	public static final String MOD_ID = "praise-salmon-god";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		MessageManager.RegisterMessages();
	}
}