package com.climateop.mendingoverhauled.client.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
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
				System.out.println("Hovered item: " + slot.getItem());
			}
		}
	}
}