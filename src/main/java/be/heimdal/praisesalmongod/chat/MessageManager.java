package be.heimdal.praisesalmongod.chat;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class MessageManager {
    public static void RegisterMessages(){
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            var messageString = message.getContent().getString().toLowerCase();
            if(messageString.contains("praise") && messageString.contains("salmon")){
                tryPraise(sender);
            }
        });
    }

    private static void tryPraise(ServerPlayerEntity player) {
        boolean isInWater = player.getWorld().getBlockState(player.getBlockPos()).getBlock() == Blocks.WATER;
        boolean isInventoryEmpty = player.getInventory().isEmpty();

        if(isInventoryEmpty && isInWater){
            player.giveItemStack(new ItemStack(Items.SALMON, 64));
            player.sendMessage(Text.literal("The Salmon God has blessed you!").formatted(Formatting.AQUA), false);
        } else {
            player.sendMessage(Text.literal("You must be in water and have an empty inventory!").formatted(Formatting.GRAY), false);
        }
    }
}
