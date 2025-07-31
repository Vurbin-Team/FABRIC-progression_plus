package com.progressionplus.sounds;

import com.progressionplus.Progressionplus;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent BUTTON_UPGRADE = registerSoundEvent("button_upgrade");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.of(Progressionplus.MOD_ID, name);

        return Registry.register(
            Registries.SOUND_EVENT,
            id,
            SoundEvent.of(id)
        );
    }

    public static void registerSounds() {
        Progressionplus.LOGGER.info("Registering mod sounds for " + Progressionplus.MOD_ID);
    }
}
