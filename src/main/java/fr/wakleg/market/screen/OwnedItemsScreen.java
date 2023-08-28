package fr.wakleg.market.screen;

import fr.wakleg.market.screen.handler.OwnedItemsScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class OwnedItemsScreen extends HandledScreen<OwnedItemsScreenHandler> {
    private static final Identifier TEXTURE = new Identifier("minecraft:textures/gui/container/generic_54.png");
    private final int ROWS;
    public OwnedItemsScreen(OwnedItemsScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.ROWS = handler.ROWS;
        this.backgroundHeight = 114 + this.ROWS * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.ROWS * 18 + 17);
        context.drawTexture(TEXTURE, i, j + this.ROWS * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }
}
