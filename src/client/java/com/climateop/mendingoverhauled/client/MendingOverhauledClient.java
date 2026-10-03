package com.climateop.mendingoverhauled.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MendingOverhauledClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.mouseHandler.isMiddlePressed()) {
				System.out.println("Middle mouse is being held!");
			}
		});
	}
}