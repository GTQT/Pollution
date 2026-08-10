package meowmel.pollution.api.metatileentity.ui;

import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.GenericSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import meowmel.pollution.api.metatileentity.MagicRecipeMapMultiblockController;
import meowmel.pollution.client.gui.AstralConstellationPanelWidget;

/** Extends the standard MUI2 multiblock screen with a live astral panel. */
public final class MagicMultiblockUIFactory extends MultiblockUIFactory {

    private static final int ASTRAL_PANEL_WIDTH = 176;
    private static final int ASTRAL_PANEL_HEIGHT = 109;
    private final MagicRecipeMapMultiblockController controller;

    public MagicMultiblockUIFactory(MagicRecipeMapMultiblockController controller) {
        super(controller);
        this.controller = controller;
    }

    @Override
    public ModularPanel buildUI(com.cleanroommc.modularui.factory.PosGuiData guiData,
                                PanelSyncManager syncManager) {
        ModularPanel panel = super.buildUI(guiData, syncManager);

        GenericSyncValue<String> state = syncManager.getOrCreateSyncHandler("pollution_astral_panel", 0,
                GenericSyncValue.class, () -> GenericSyncValue.builder(String.class)
                        .getter(controller::getAstralPanelState)
                        .serializer((buffer, value) -> buffer.writeString(value))
                        .deserializer(buffer -> buffer.readString(256))
                        .copyImmutable()
                        .build());
        panel.child(new AstralConstellationPanelWidget(state)
                // Keep GT's stock screen, inventory and side controls in their
                // original coordinates. This read-only panel is deliberately
                // rendered into the free space to the left of the main GUI.
                .pos(-ASTRAL_PANEL_WIDTH - 8, 4)
                .size(ASTRAL_PANEL_WIDTH, ASTRAL_PANEL_HEIGHT));
        return panel;
    }
}
