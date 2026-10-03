package com.climateop.mendingoverhauled.client.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class ExampleClientMixin {

	@Invoker("getHoveredSlot")
	public abstract Slot mendingOverhauled$getHoveredSlot(double x, double y);

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void onMouseClicked(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
		if (event.button() == 2) {
			Slot slot = mendingOverhauled$getHoveredSlot(event.x(), event.y());

			if (slot != null) {
				Holder<Enchantment> mending = slot.getItem()
						.getEnchantments()
						.keySet()
						.stream()
						.filter(holder -> holder.is(Enchantments.MENDING))
						.findFirst()
						.orElse(null);

				if (mending != null && slot.getItem().isDamaged()) {
					Minecraft minecraft = Minecraft.getInstance();

					if (minecraft.player != null) {
						int availableXp = getAvailableXp(minecraft.player);

						int durabilityNeeded = slot.getItem().getDamageValue();

						int xpRequired = (int) Math.ceil(durabilityNeeded / 2.0);

						int xpToUse = Math.min(availableXp, xpRequired);
						int durabilityToRepair = xpToUse * 2;

						minecraft.player.giveExperiencePoints(-xpToUse);

						slot.getItem().setDamageValue(
								Math.max(0, slot.getItem().getDamageValue() - durabilityToRepair)
						);

						System.out.println("XP used: " + xpToUse);
						System.out.println("Durability repaired: " + durabilityToRepair);
					}
				}
			}
		}
	}

	@Unique
	private int getAvailableXp(net.minecraft.client.player.LocalPlayer player) {
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

		xp += (int) (player.experienceProgress * player.getXpNeededForNextLevel());

		return xp;
	}
}