package xyz.quazaros.allitems73.client.inventory;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import xyz.quazaros.allitems73.client.Allitems73Client;
import xyz.quazaros.allitems73.client.items.item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xyz.quazaros.allitems73.client.events.onClickEvent.onInventoryKeyPressed;

public class VirtualChestScreen extends Screen {
    private static final int VISIBLE_ROWS = 5;
    private static final int COLUMNS = 9;

    private static final Identifier TEXTURE =
            Identifier.of("minecraft", "textures/gui/container/creative_inventory/tab_items.png");

    private static final int BACKGROUND_WIDTH = 195;
    private static final int BACKGROUND_HEIGHT = 136;
    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 256;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_OFFSET_X = 9;
    private static final int SLOT_OFFSET_Y = 18;

    private int guiLeft;
    private int guiTop;

    public final DefaultedList<ItemStack> stacks =
            DefaultedList.ofSize(Allitems73Client.ItemList.getSize(), ItemStack.EMPTY);

    private boolean filtered;

    public VirtualChestScreen(boolean filtered) {
        super(Text.literal(!filtered ? "All Items Inventory": "All Items Inventory - Filtered"));
        this.filtered = filtered;
    }

    @Override
    public void init() {
        this.guiLeft = (this.width - BACKGROUND_WIDTH) / 2;
        this.guiTop = (this.height - BACKGROUND_HEIGHT) / 2;

        if (!filtered) {
            for (int i = 0; i < stacks.size(); i++) {
                stacks.set(i, Allitems73Client.ItemList.items.get(i).item_stack);
            }
        } else {
            ArrayList<item> filteredItemList = Allitems73Client.ItemList.getFilteredList();
            for (int i = 0; i < stacks.size(); i++) {
                stacks.set(i, filteredItemList.get(i).item_stack);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        renderBackgroundTexture(context);
        drawTitle(context);
        renderSlotsAndItems(context);
        renderHoveredTooltip(context, mouseX, mouseY);
        renderScrollbar(context);
        renderProgress(context, mouseX, mouseY);
        renderFilter(context, mouseX, mouseY);
    }

    private void renderBackgroundTexture(DrawContext context) {
        context.getMatrices().push();
        context.drawTexture(
                TEXTURE,
                guiLeft, guiTop,
                0.0f, 0.0f,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TEX_WIDTH, TEX_HEIGHT
        );
        context.getMatrices().pop();
    }

    private void renderSlotsAndItems(DrawContext context) {
        for (int visRow = 0; visRow < VISIBLE_ROWS; visRow++) {
            int row = visRow + scrollOffsetRow;
            for (int col = 0; col < COLUMNS; col++) {
                int index = row * COLUMNS + col;
                if (index < 0 || index >= stacks.size()) {
                    continue;
                }
                ItemStack stack = stacks.get(index);
                int x = guiLeft + SLOT_OFFSET_X + col * SLOT_SIZE;
                int y = guiTop  + SLOT_OFFSET_Y + visRow * SLOT_SIZE;
                if (!stack.isEmpty()) {
                    context.drawItem(stack, x, y);
                }
            }
        }
    }

    private void drawTitle(DrawContext context) {
        int titleX = guiLeft + 8;
        int titleY = guiTop + 6;
        context.drawText(
                this.textRenderer,
                this.title,
                titleX,
                titleY,
                0xFF404040,
                false
        );
    }

    private void renderHoveredTooltip(DrawContext context, int mouseX, int mouseY) {
        int slotIndex = getSlotIndexAt(mouseX, mouseY);
        if (slotIndex < 0) return;
        if (slotIndex >= stacks.size()) return;

        ItemStack stack = stacks.get(slotIndex);
        if (stack.isEmpty()) return;

        item tempItem = Allitems73Client.ItemList.get(stack.getItem().toString());

        List<Text> lines = new ArrayList<>();
        lines.add(Text.literal(tempItem.item_display_name).formatted(tempItem.is_found ? Formatting.GREEN : Formatting.RED));

        context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
    }

    private int getSlotIndexAt(int mouseX, int mouseY) {
        int gridLeft = guiLeft + SLOT_OFFSET_X;
        int gridTop  = guiTop  + SLOT_OFFSET_Y;
        int gridRight  = gridLeft + COLUMNS * SLOT_SIZE;
        int gridBottom = gridTop  + VISIBLE_ROWS * SLOT_SIZE; // or ROWS if no scroll
        if (mouseX < gridLeft || mouseX >= gridRight || mouseY < gridTop || mouseY >= gridBottom) {
            return -1;
        }
        int col = (mouseX - gridLeft) / SLOT_SIZE;
        int visRow = (mouseY - gridTop) / SLOT_SIZE;
        // If you have scrolling, convert visible row -> actual row
        int row = visRow + scrollOffsetRow; // if you use scrollOffsetRow
        int index = row * COLUMNS + col;
        // Bounds check against your stacks list
        if (index < 0 || index >= stacks.size()) {
            return -1;
        }
        return index;
    }

    private void renderProgress(DrawContext context, int mouseX, int mouseY) {
        ItemStack progressStack = new ItemStack(Items.DIAMOND);

        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 3;
        int y = guiTop  + SLOT_OFFSET_Y + VISIBLE_ROWS * SLOT_SIZE + 4;

        context.drawItem(progressStack, x, y);

        int size = 16;
        if (mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size) {
            List<Text> lines = new ArrayList<>();
            lines.add(Text.literal("Progress: " + Allitems73Client.ItemList.getProgString()).formatted(Formatting.AQUA));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
        }
    }

    private void renderFilter(DrawContext context, int mouseX, int mouseY) {
        ItemStack progressStack = new ItemStack(Items.HOPPER);

        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 5;
        int y = guiTop  + SLOT_OFFSET_Y + VISIBLE_ROWS * SLOT_SIZE + 4;

        context.drawItem(progressStack, x, y);

        int size = 16;
        if (mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size) {
            List<Text> lines = new ArrayList<>();
            lines.add(Text.literal("Filter").formatted(Formatting.AQUA));
            context.drawTooltip(this.textRenderer, lines, mouseX, mouseY);
        }
    }

    private boolean isOverFilter(double mouseX, double mouseY) {
        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 5;
        int y = guiTop  + SLOT_OFFSET_Y + VISIBLE_ROWS * SLOT_SIZE + 4;
        return mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && canScroll() && isOverScrollbar(mouseX, mouseY)) {
            scrolling = true;
            updateScrollFromMouse(mouseY);
            return true;
        } else if (button == 0 && isOverFilter(mouseX, mouseY)) {
            onInventoryKeyPressed(MinecraftClient.getInstance(), !filtered);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /////////////////////
    // Scrollbar Stuff //
    /////////////////////

    private float scrollPosition = 0.0f;
    private int scrollOffsetRow = 0;
    private boolean scrolling = false;

    private static final int SCROLLBAR_X = 175;
    private static final int SCROLLBAR_Y = 18;
    private static final int SCROLLBAR_HEIGHT = 110;
    private static final int SCROLLBAR_WIDTH = 12;
    private static final int KNOB_HEIGHT = 15;

    private static final int KNOB_TEX_HEIGHT = 15;
    private static final int KNOB_TEX_WIDTH = 12;

    private static final Identifier KNOB_TEXTURE =
            Identifier.of("minecraft", "textures/gui/sprites/container/creative_inventory/scroller.png");
    private static final Identifier KNOB_TEXTURE_DISABLES =
            Identifier.of("minecraft", "textures/gui/sprites/container/creative_inventory/scroller_disabled.png");

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!canScroll()) {
            return false;
        }

        float dir = (float) verticalAmount;
        int maxRows = getMaxScrollRows();
        if (maxRows <= 0) return false;

        float step = 1.0f / maxRows;
        scrollPosition = clamp(scrollPosition - dir * step, 0.0f, 1.0f);
        updateScrollFromPosition();
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && scrolling && canScroll()) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            scrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void renderScrollbar(DrawContext context) {
        Identifier tempTexture;
        if (!canScroll()) {
            tempTexture = KNOB_TEXTURE_DISABLES;
        } else {
            tempTexture = KNOB_TEXTURE;
        }

        int x = guiLeft + SCROLLBAR_X;
        int y = guiTop  + SCROLLBAR_Y;

        context.fill(x, y, x + SCROLLBAR_WIDTH, y + SCROLLBAR_HEIGHT, 0x00202020);

        int trackHeight = SCROLLBAR_HEIGHT - KNOB_HEIGHT;
        int knobY = y + (int) (scrollPosition * trackHeight);

        context.drawTexture(
                tempTexture,
                x, knobY,
                KNOB_TEX_WIDTH,
                KNOB_TEX_HEIGHT,
                KNOB_TEX_WIDTH,
                KNOB_TEX_HEIGHT,
                KNOB_TEX_WIDTH,
                KNOB_TEX_HEIGHT
        );
    }

    private int getTotalRows() {
        return (int) Math.ceil(stacks.size() / (double) COLUMNS);
    }

    private int getMaxScrollRows() {
        return Math.max(0, getTotalRows() - VISIBLE_ROWS);
    }

    private boolean canScroll() {
        return getMaxScrollRows() > 0;
    }

    private void updateScrollFromPosition() {
        int max = getMaxScrollRows();
        if (max <= 0) {
            scrollOffsetRow = 0;
            scrollPosition = 0.0f;
            return;
        }
        scrollOffsetRow = Math.round(scrollPosition * max);
    }

    private static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    private boolean isOverScrollbar(double mouseX, double mouseY) {
        int x = guiLeft + SCROLLBAR_X;
        int y = guiTop  + SCROLLBAR_Y;
        return mouseX >= x && mouseX < x + SCROLLBAR_WIDTH
                && mouseY >= y && mouseY < y + SCROLLBAR_HEIGHT;
    }

    private void updateScrollFromMouse(double mouseY) {
        int y = guiTop + SCROLLBAR_Y;
        int trackHeight = SCROLLBAR_HEIGHT - KNOB_HEIGHT;
        float relative = (float) ((mouseY - y - KNOB_HEIGHT / 2.0) / trackHeight);
        scrollPosition = clamp(relative, 0.0f, 1.0f);
        updateScrollFromPosition();
    }
}