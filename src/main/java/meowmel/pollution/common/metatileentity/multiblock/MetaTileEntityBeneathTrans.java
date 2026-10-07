package meowmel.pollution.common.metatileentity.multiblock;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MetaTileEntityBaseWithControl;
import gregtech.api.metatileentity.multiblock.ui.MultiblockUIFactory;
import gregtech.api.mui.GTGuiTextures;
import gregtech.api.mui.GTGuis;
import gregtech.api.pattern.casing.DeclarativePatternBuilder;
import gregtech.api.pattern.element.StructureDefinition;
import gregtech.api.unification.material.Materials;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import gregtech.common.blocks.BlockBoilerCasing;
import gregtech.common.blocks.BlockMetalCasing;
import gregtech.common.blocks.MetaBlocks;
import meowmel.pollution.api.utils.POBeneathTeleporter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import static gregtech.api.pattern.element.Elements.any;

public class MetaTileEntityBeneathTrans extends MetaTileEntityBaseWithControl {

    private static final StructureDefinition<?> TEMPLATE = StructureDefinition.getOrBuild("pollution:beneath_trans", () ->
            DeclarativePatternBuilder.start()
                    .aisle("AA     AA", "AA     AA", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
                    .aisle("AAAAAAAAA", "AA     AA", " B     B ", " B     B ", " B     B ", " B     B ", " B     B ", " B     B ", "         ", "         ", "         ", "         ")
                    .aisle(" AAAAAAA ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
                    .aisle(" AAAPAAA ", "   AAA   ", "   AAA   ", "   B B   ", "   B B   ", "   BBB   ", "   B B   ", "   B B   ", "   B B   ", "   B B   ", "   AAA   ", "   AAA   ")
                    .aisle(" AAPPPAA ", "   APA   ", "   APA   ", "    P    ", "    P    ", "   BPB   ", "    P    ", "    P    ", "    P    ", "    P    ", "   APA   ", "   APA   ")
                    .aisle(" AAAPAAA ", "   AAA   ", "   ASA   ", "   B B   ", "   B B   ", "   BBB   ", "   B B   ", "   B B   ", "   B B   ", "   B B   ", "   AAA   ", "   AAA   ")
                    .aisle(" AAAAAAA ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")
                    .aisle("AAAAAAAAA", "AA     AA", " B     B ", " B     B ", " B     B ", " B     B ", " B     B ", " B     B ", "         ", "         ", "         ", "         ")
                    .aisle("AA     AA", "AA     AA", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ", "         ")

                    .self('S', MetaTileEntityBeneathTrans.class)
                    .blocks('A', getCasingState())
                    .blocks('P', getPipeState())
                    .frames('B', Materials.Steel)
                    .where(' ', any())
                    .buildStructureDefinition()
    );

    public MetaTileEntityBeneathTrans(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
    }

    public static IBlockState getCasingState() {
        return MetaBlocks.METAL_CASING.getState(BlockMetalCasing.MetalCasingType.STEEL_SOLID);
    }

    public static IBlockState getPipeState() {
        return MetaBlocks.BOILER_CASING.getState(BlockBoilerCasing.BoilerCasingType.STEEL_PIPE);
    }

    @Override
    protected void updateFormedValid() {

    }

    @Override
    protected @NotNull StructureDefinition<?> createStructureDefinition() {
        return TEMPLATE;
    }

    @Override
    public boolean hasMufflerMechanics() {
        return false;
    }

    @Override
    public boolean hasMaintenanceMechanics() {
        return false;
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return Textures.SOLID_STEEL_CASING;
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityBeneathTrans(this.metaTileEntityId);
    }

    @Override
    protected MultiblockUIFactory createUIFactory() {
        return super.createUIFactory()
                .createFlexButton((guiData, syncManager) -> {
                    var panel = syncManager.syncedPanel("beneath_trans_panel", true, this::makeTeleportPanel);

                    return new ButtonWidget<>()
                            .size(18)
                            .overlay(GTGuiTextures.FILTER_SETTINGS_OVERLAY.asIcon().size(16))
                            .addTooltipLine(IKey.lang("pollution.machine.beneath_trans.button.tooltip"))
                            .onMousePressed(mouseButton -> {
                                if (panel.isPanelOpen()) {
                                    panel.closePanel();
                                } else {
                                    panel.openPanel();
                                }
                                return true;
                            });
                });
    }

    /**
     * 盾构面板：中间只有一个长方形按钮，点击后把玩家送到地下世界落点平台。
     *
     * <p>按钮本身不做事，它改的是 {@code BooleanSyncValue} 的值——真正的传送发生在
     * sync value 的 setter 里，由服务端执行。</p>
     */
    private ModularPanel makeTeleportPanel(PanelSyncManager syncManager, IPanelHandler syncHandler) {
        BooleanSyncValue teleport = new BooleanSyncValue(() -> false, requested -> {
            if (!requested) return;

            EntityPlayer player = syncManager.getPlayer();
            if (player != null) POBeneathTeleporter.sendToBeneath(player);
        });
        syncManager.syncValue("beneath_teleport", teleport);

        return GTGuis.createPopupPanel("beneath_trans_teleport", 136, 46)
                .child(Flow.row()
                        .pos(6, 6)
                        .height(16)
                        .coverChildrenWidth()
                        .child(IKey.lang("pollution.machine.beneath_trans.panel.title")
                                .asWidget()
                                .heightRel(1.0f)))
                .child(new ButtonWidget<>()
                        .pos(8, 22)
                        .size(120, 18)
                        .overlay(IKey.lang("pollution.machine.beneath_trans.button.teleport"))
                        .addTooltipLine(IKey.lang("pollution.machine.beneath_trans.button.teleport.tooltip"))
                        .onMousePressed(mouseButton -> {
                            teleport.setBoolValue(true, true, true);
                            return true;
                        }));
    }
}
