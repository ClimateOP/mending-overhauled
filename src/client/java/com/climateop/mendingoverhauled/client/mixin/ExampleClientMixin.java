package com.climateop.mendingoverhauled.client.mixin;

import com.climateop.mendingoverhauled.MendingRepairPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
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

	@Invoker("getHoveredSlot")
	public abstract Slot mendingOverhauled$getHoveredSlot(
			double x,
			double y
	);

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void onMouseClicked(
			MouseButtonEvent event,
			boolean doubleClick,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (event.button() == 2) {
			mendingOverhauled$middleMouseHeld = true;
		}
	}

	@Inject(method = "mouseReleased", at = @At("HEAD"))
	private void onMouseReleased(
			MouseButtonEvent event,
			CallbackInfoReturnable<Boolean> cir
	) {
		if (event.button() == 2) {
			mendingOverhauled$middleMouseHeld = false;
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

		double mouseX = minecraft.mouseHandler.xpos();
		double mouseY = minecraft.mouseHandler.ypos();

		double scaledX = mouseX
				* minecraft.getWindow().getGuiScaledWidth()
				/ minecraft.getWindow().getWidth();

		double scaledY = mouseY
				* minecraft.getWindow().getGuiScaledHeight()
				/ minecraft.getWindow().getHeight();

		Slot slot = mendingOverhauled$getHoveredSlot(
				scaledX,
				scaledY
		);

		if (slot == null) {
			return;
		}

		AbstractContainerScreen<?> screen =
				(AbstractContainerScreen<?>) (Object) this;

		int slotIndex = screen.getMenu().slots.indexOf(slot);

		if (slotIndex < 0) {
			return;
		}

		ClientPlayNetworking.send(
				new MendingRepairPayload(slotIndex)
		);
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void onRemoved(CallbackInfo ci) {
		mendingOverhauled$middleMouseHeld = false;
	}
}