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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class ExampleClientMixin {

	@Unique
	private boolean mendingOverhauled$middleMouseHeld = false;

	@Unique
	private double mendingOverhauled$mouseX = 0;

	@Unique
	private double mendingOverhauled$mouseY = 0;

	@Invoker("getHoveredSlot")
	public abstract Slot mendingOverhauled$getHoveredSlot(double x, double y);

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void onMouseClicked(
			MouseButtonEvent event,
			boolean doubleClick,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (event.button() == 2) {
			mendingOverhauled$middleMouseHeld = true;

			mendingOverhauled$mouseX = event.x();
			mendingOverhauled$mouseY = event.y();

			System.out.println("Middle mouse held!");
		}
	}

	@Inject(method = "mouseReleased", at = @At("HEAD"))
	private void onMouseReleased(
			MouseButtonEvent event,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (event.button() == 2) {
			mendingOverhauled$middleMouseHeld = false;

			System.out.println("Middle mouse released!");
		}
	}

	@Inject(method = "containerTick", at = @At("HEAD"))
	private void onContainerTick(CallbackInfo ci) {
		if (!mendingOverhauled$middleMouseHeld) {
			return;
		}

		Minecraft minecraft = Minecraft.getInstance();

		if (minecraft.player == null) {
			return;
		}

		Slot slot = mendingOverhauled$getHoveredSlot(
				mendingOverhauled$mouseX,
				mendingOverhauled$mouseY
		);

		if (slot == null || slot.getItem().isEmpty()) {
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

		int availableXp = getAvailableXp(minecraft.player);

		if (availableXp <= 0) {
			return;
		}

		// 7 XP per tick = 14 durability per tick
		// 20 ticks per second = 280 durability per second
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

		minecraft.player.giveExperiencePoints(-xpToUse);

		slot.getItem().setDamageValue(
				Math.max(
						0,
						currentDamage - durabilityToRepair
				)
		);
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void onRemoved(CallbackInfo ci) {
		mendingOverhauled$middleMouseHeld = false;
	}

	@Unique
	private int getAvailableXp(
			net.minecraft.client.player.LocalPlayer player
	) {
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
}