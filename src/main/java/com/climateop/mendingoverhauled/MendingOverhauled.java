package com.climateop.mendingoverhauled;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MendingOverhauled implements ModInitializer {

	public static final String MOD_ID = "mending-overhauled";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.serverboundPlay().register(
				MendingRepairPayload.TYPE,
				MendingRepairPayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(
				MendingRepairPayload.TYPE,
				(payload, context) -> {
					ServerPlayer player = context.player();

					int slotIndex = payload.slotIndex();

					if (slotIndex < 0 ||
							slotIndex >= player.containerMenu.slots.size()) {
						return;
					}

					Slot slot = player.containerMenu.slots.get(slotIndex);

					if (slot.getItem().isEmpty()) {
						return;
					}

					Holder<Enchantment> mending = slot.getItem()
							.getEnchantments()
							.keySet()
							.stream()
							.filter(holder -> holder.is(Enchantments.MENDING))
							.findFirst()
							.orElse(null);

					if (mending == null || !slot.getItem().isDamaged()) {
						return;
					}

					int availableXp = getAvailableXp(player);

					if (availableXp <= 0) {
						return;
					}

					// 7 XP per tick
					// 1 XP = 2 durability
					// 14 durability per tick
					int xpToUse = Math.min(7, availableXp);

					int durabilityToRepair = xpToUse * 2;

					int currentDamage = slot.getItem().getDamageValue();

					durabilityToRepair = Math.min(
							durabilityToRepair,
							currentDamage
					);

					if (durabilityToRepair <= 0) {
						return;
					}

					xpToUse = (durabilityToRepair + 1) / 2;

					player.giveExperiencePoints(-xpToUse);

					slot.getItem().setDamageValue(
							Math.max(
									0,
									currentDamage - durabilityToRepair
							)
					);

					slot.setChanged();

					player.containerMenu.broadcastChanges();
				}
		);
	}

	private int getAvailableXp(ServerPlayer player) {
		int xp = 0;

		for (int level = 0; level < player.experienceLevel; level++) {
			if (level >= 30) {
				xp += 112 + (level - 30) * 9;
			} else if (level >= 15) {
				xp += 37 + (level - 15) * 5;
			} else {
				xp += 7 + level * 2;
			}
		}

		xp += (int) (
				player.experienceProgress
						* player.getXpNeededForNextLevel()
		);

		return xp;
	}

	public static net.minecraft.resources.Identifier id(String path) {
		return net.minecraft.resources.Identifier.fromNamespaceAndPath(
				MOD_ID,
				path
		);
	}
}