package xyz.quazaros.allitems73.client.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.NonNullList;
import xyz.quazaros.allitems73.client.Allitems73Client;
import xyz.quazaros.allitems73.client.items.item;

import java.util.ArrayList;
import java.util.List;

import static xyz.quazaros.allitems73.client.events.onClickEvent.onInventoryKeyPressed;

public class VirtualChestScreen extends Screen {

    private static final int VISIBLE_ROWS = 5;
    private static final int COLUMNS = 9;

    // Confirmed from your Screen.class: Identifier is the correct type.
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("minecraft", "textures/gui/container/creative_inventory/tab_items.png");

    private static final int BACKGROUND_WIDTH  = 195;
    private static final int BACKGROUND_HEIGHT = 136;
    private static final int SLOT_SIZE         = 18;
    private static final int SLOT_OFFSET_X     = 9;
    private static final int SLOT_OFFSET_Y     = 18;

    private int guiLeft;
    private int guiTop;

    public final NonNullList<ItemStack> stacks =
            NonNullList.withSize(Allitems73Client.ItemList.getSize(), ItemStack.EMPTY);

    private boolean filtered;

    public VirtualChestScreen(boolean filtered) {
        super(Component.literal(!filtered ? "All Items Inventory" : "All Items Inventory - Filtered"));
        this.filtered = filtered;
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width  - BACKGROUND_WIDTH)  / 2;
        this.guiTop  = (this.height - BACKGROUND_HEIGHT) / 2;

        if (!filtered) {
            for (int i = 0; i < stacks.size(); i++) {
                stacks.set(i, Allitems73Client.ItemList.items.get(i).item_stack);
            }
        } else {
            ArrayList<item> filteredItemList = Allitems73Client.ItemList.getFilteredList();
            for (int i = 0; i < filteredItemList.size() && i < stacks.size(); i++) {
                stacks.set(i, filteredItemList.get(i).item_stack);
            }
        }
    }

    /**
     * In this version, render(...) is gone.
     * We use extractRenderState to feed the new rendering pipeline.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // We don't call super.extractRenderState here if we are doing manual rendering
        // unless we have added widgets via addRenderableWidget.

        renderBackgroundTexture(graphics);
        drawTitle(graphics);
        renderSlotsAndItems(graphics);
        renderHoveredTooltip(graphics, mouseX, mouseY);
        renderScrollbar(graphics);
        renderProgress(graphics, mouseX, mouseY);
        renderFilter(graphics, mouseX, mouseY);

        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void renderBackgroundTexture(GuiGraphicsExtractor graphics) {
        // Use blit with the GUI_TEXTURED pipeline as seen in your Screen.class
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                guiLeft, guiTop,
                0.0f, 0.0f,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                256, 256 // Assuming standard texture size
        );
    }

    private void renderSlotsAndItems(GuiGraphicsExtractor graphics) {
        for (int visRow = 0; visRow < VISIBLE_ROWS; visRow++) {
            int row = visRow + scrollOffsetRow;
            for (int col = 0; col < COLUMNS; col++) {
                int index = row * COLUMNS + col;
                if (index < 0 || index >= stacks.size()) continue;

                ItemStack stack = stacks.get(index);
                int x = guiLeft + SLOT_OFFSET_X + col    * SLOT_SIZE;
                int y = guiTop  + SLOT_OFFSET_Y + visRow * SLOT_SIZE;

                if (!stack.isEmpty()) {
                    // drawItem is now renderItem in the extractor
                    graphics.item(stack, x, y);
                }
            }
        }
    }

    private void drawTitle(GuiGraphicsExtractor graphics) {
        // Using this.font and drawString as standard for 26.1
        //graphics.drawString(this.font, this.title, guiLeft + 8, guiTop + 6, 0xFF404040, false);
    }

    private void renderHoveredTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int slotIndex = getSlotIndexAt(mouseX, mouseY);
        if (slotIndex < 0 || slotIndex >= stacks.size()) return;

        ItemStack stack = stacks.get(slotIndex);
        if (stack.isEmpty()) return;

        item tempItem = Allitems73Client.ItemList.get(stack.getItem().toString());

        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(tempItem.item_display_name)
                .withStyle(tempItem.is_found ? ChatFormatting.GREEN : ChatFormatting.RED));

        // drawTooltip is now renderComponentTooltip
        graphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
    }

    // --- Interaction Logic (Primitive types mouseX/mouseY) ---

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseEvent, boolean doubleClicked) {
        var button = mouseEvent.button();
        double mouseX = mouseEvent.x();
        double mouseY = mouseEvent.y();

        if (button == 0) {
            if (canScroll() && isOverScrollbar(mouseX, mouseY)) {
                scrolling = true;
                updateScrollFromMouse(mouseY);
                return true;
            } else if (isOverFilter(mouseX, mouseY)) {
                onInventoryKeyPressed(Minecraft.getInstance(), !filtered);
                return true;
            }
        }
        return super.mouseClicked(mouseEvent, doubleClicked);
    }

    // --- Scrollbar & Utility Logic ---

    private float scrollPosition = 0.0f;
    private int scrollOffsetRow = 0;
    private boolean scrolling = false;

    private void renderScrollbar(GuiGraphicsExtractor graphics) {
        Identifier sprite = canScroll() ?
                Identifier.withDefaultNamespace("container/creative_inventory/scroller") :
                Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");

        int x = guiLeft + 175;
        int trackHeight = 110 - 15;
        int knobY = guiTop + 18 + (int) (scrollPosition * trackHeight);

        // Using blitSprite for the modern UI system
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, knobY, 12, 15);
    }

    private int getSlotIndexAt(int mouseX, int mouseY) {
        int gridLeft = guiLeft + SLOT_OFFSET_X;
        int gridTop  = guiTop  + SLOT_OFFSET_Y;
        if (mouseX < gridLeft || mouseX >= gridLeft + COLUMNS * SLOT_SIZE ||
                mouseY < gridTop  || mouseY >= gridTop + VISIBLE_ROWS * SLOT_SIZE) return -1;

        int col = (mouseX - gridLeft) / SLOT_SIZE;
        int visRow = (mouseY - gridTop) / SLOT_SIZE;
        return (visRow + scrollOffsetRow) * COLUMNS + col;
    }

    private void renderProgress(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 3;
        int y = guiTop + 110;
        graphics.item(new ItemStack(Items.DIAMOND), x, y);
    }

    private void renderFilter(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 5;
        int y = guiTop + 110;
        graphics.item(new ItemStack(Items.HOPPER), x, y);
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= guiLeft + 175 && mouseX < guiLeft + 187 && mouseY >= guiTop + 18 && mouseY < guiTop + 128;
    }

    private boolean isOverFilter(double mouseX, double mouseY) {
        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 5;
        int y = guiTop + 110;
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private void updateScrollFromMouse(double mouseY) {
        int trackHeight = 110 - 15;
        float relative = (float) ((mouseY - (guiTop + 18) - 7.5) / trackHeight);
        scrollPosition = Math.max(0.0f, Math.min(1.0f, relative));
        int totalRows = (int) Math.ceil(stacks.size() / 9.0);
        scrollOffsetRow = Math.round(scrollPosition * Math.max(0, totalRows - VISIBLE_ROWS));
    }

    private boolean canScroll() {
        return stacks.size() > VISIBLE_ROWS * COLUMNS;
    }
}