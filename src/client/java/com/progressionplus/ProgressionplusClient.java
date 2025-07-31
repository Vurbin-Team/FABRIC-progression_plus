package com.progressionplus;

import com.progressionplus.data.PlayerComponents;
import com.progressionplus.block.renderer.ExperienceStorageBlockEntityRenderer;
import com.progressionplus.gui.CustomHudRenderer;
import com.progressionplus.config.HudConfigLoader;
import com.progressionplus.gui.UpgradeMenu;
import com.progressionplus.network.ClientModMessages;
import com.progressionplus.upgrade.UpgradeType;
import com.progressionplus.registry.ModBlocks;
import com.progressionplus.registry.block.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ProgressionplusClient implements ClientModInitializer {
	private static KeyBinding openMenuKey;

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(new CustomHudRenderer());
		HudConfigLoader.load();

		// Register the key binding
		openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.progression-plus.open_menu",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_U,
				"category.progression-plus.keys"
		));

		// Register a tick event to check for key presses
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (openMenuKey.wasPressed()) {
				// Open the custom screen
				MinecraftClient.getInstance().setScreen(new UpgradeMenu());
			}
		});


		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.YELLOW_SKINT_CRYSTAL, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EXPERIENCE_STORAGE_PEDESTAL, RenderLayer.getCutout());

		BlockEntityRendererRegistry.register(
				ModBlockEntities.EXPERIENCE_PEDESTAL_BE,
				ExperienceStorageBlockEntityRenderer::new
		);

		// Initialize client-side networking
		ClientModMessages.initClient();
	}
}