package dev.seeruk.monsooncraft.screen;

import dev.seeruk.monsooncraft.block.CompressorBlockEntity;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.NotNull;

public class CompressorBlockGuiDescription extends SyncedGuiDescription {
    public CompressorBlockGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Screens.COMPRESSOR_SCREEN_TYPE, syncId, playerInventory, getBlockInventory(context, 2), getBlockPropertyDelegate(context, 1));

        var root = new WGridPanel();
        root.setInsets(Insets.ROOT_PANEL);

        var inputSlot = WItemSlot.of(blockInventory, CompressorBlockEntity.INPUT_SLOT);
        root.add(inputSlot, 3, 1);

        var outputSlot = WItemSlot.of(blockInventory, CompressorBlockEntity.OUTPUT_SLOT);
        root.add(outputSlot, 5, 1);

        var modeLabel = getDynamicLabel();
        root.add(modeLabel, 6, 0, 3, 1);

        root.add(this.createPlayerInventoryPanel(), 0, 3);

        // Finalise
        setRootPanel(root);
        root.validate(this);
    }

    @NotNull
    private WDynamicLabel getDynamicLabel() {
        var modeLabel = new WDynamicLabel(() -> {
            var mode = this.getPropertyDelegate().get(CompressorBlockEntity.MODE_PROPERTY);

            if (mode == CompressorBlockEntity.MODE_2x2) {
                return I18n.translate("block.monsooncraft.compressor.2x2_mode");
            }

            if (mode == CompressorBlockEntity.MODE_3x3) {
                return I18n.translate("block.monsooncraft.compressor.3x3_mode");
            }

            return "?x?"; // Should not happen!?
        });

        modeLabel.setAlignment(HorizontalAlignment.RIGHT);

        return modeLabel;
    }
}
