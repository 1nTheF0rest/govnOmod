package ru.govnomod;

import net.fabricmc.api.ClientModInitializer;

public final class GovnOmodClient implements ClientModInitializer {
    public static final String MOD_ID = "govnomod";
    public static final Config CONFIG = Config.load();

    @Override
    public void onInitializeClient() {
        CONFIG.save();
    }
}
