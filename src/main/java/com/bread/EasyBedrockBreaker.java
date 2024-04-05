package com.bread;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.text.*;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class EasyBedrockBreaker implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("easy-bedrock-breaker");

	private static ArrayList<Packet<?>> delayedPackets = new ArrayList<>();

	private static KeyBinding activateKey;

	public static final Class[] blockedPackets = {
			PlayerActionC2SPacket.class,
			PlayerInputC2SPacket.class,
			PlayerInteractBlockC2SPacket.class,
			PlayerInteractItemC2SPacket.class,
			QueryBlockNbtC2SPacket.class,
			UpdatePlayerAbilitiesC2SPacket.class
	};

	@Override
	public void onInitializeClient() {
		activateKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.bread.delayBlockPackets", InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "category.bread.breadclient"));
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (!activateKey.isPressed()) releasePackets();
		});

		LOGGER.info("easy bedrock breaker initialized");
	}

	public static boolean isDelayingPackets() {
		return activateKey.isPressed();
	}

	public static void delayPacket(Packet<?> p) {
		delayedPackets.add(p);
	}

	public static void clearPackets() {
		delayedPackets.clear();
	}

	private void releasePackets() {
		for (Packet<?> packet : delayedPackets) {
			MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
		}
		delayedPackets.clear();
	}

}