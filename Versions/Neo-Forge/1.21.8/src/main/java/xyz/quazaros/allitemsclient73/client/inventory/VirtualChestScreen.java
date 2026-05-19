package xyz.quazaros.allitemsclient73.client.inventory;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;

import xyz.quazaros.allitemsclient73.items.item;
import xyz.quazaros.allitemsclient73.main;

import java.util.ArrayList;
import java.util.List;

import static xyz.quazaros.allitemsclient73.client.events.onClickEvent.onInventoryKeyPressed;

public class VirtualChestScreen extends Screen {
    private static final int VISIBLE_ROWS = 5;
    private static final int COLUMNS = 9;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/creative_inventory/tab_items.png");

    private static final int BACKGROUND_WIDTH = 195;
    private static final int BACKGROUND_HEIGHT = 136;
    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 256;
    private static final int SLOT_SIZE = 18;
    private static final int SLOT_OFFSET_X = 9;
    private static final int SLOT_OFFSET_Y = 18;

    private int guiLeft;
    private int guiTop;

    public final NonNullList<ItemStack> stacks =
            NonNullList.withSize(main.ItemList.getSize(), ItemStack.EMPTY);

    private boolean filtered;

    private List<ClientTooltipComponent> pendingTooltip;
    private int pendingTooltipX;
    private int pendingTooltipY;

    public VirtualChestScreen(boolean filtered) {
        super(
                Component.literal(
                        !filtered ?
                                "All Items Inventory" + (ModList.get().isLoaded("allitems73") ? " (C)" : "") :
                                "All Items Inventory - Filtered" + (ModList.get().isLoaded("allitems73") ? " (C)" : "")
                )
        );
        this.filtered = filtered;
    }

    @Override
    public void init() {
        this.guiLeft = (this.width - BACKGROUND_WIDTH) / 2;
        this.guiTop = (this.height - BACKGROUND_HEIGHT) / 2;

        if (!filtered) {
            for (int i = 0; i < stacks.size(); i++) {
                stacks.set(i, main.ItemList.items.get(i).item_stack);
            }
        } else {
            ArrayList<item> filteredItemList = main.ItemList.getFilteredList();
            for (int i = 0; i < stacks.size(); i++) {
                stacks.set(i, filteredItemList.get(i).item_stack);
            }
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        pendingTooltip = null;

        renderBackgroundTexture(context);
        drawTitle(context);
        renderSlotsAndItems(context);
        renderScrollbar(context);
        renderProgress(context, mouseX, mouseY);
        renderFilter(context, mouseX, mouseY);
        renderHoveredTooltip(context, mouseX, mouseY);

        if (pendingTooltip != null) {
            context.renderTooltip(
                    this.font,
                    pendingTooltip,
                    pendingTooltipX,
                    pendingTooltipY,
                    DefaultTooltipPositioner.INSTANCE,
                    null
            );
        }
    }

    private void renderBackgroundTexture(GuiGraphics context) {
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                guiLeft, guiTop,
                0.0f, 0.0f,
                BACKGROUND_WIDTH, BACKGROUND_HEIGHT,
                TEX_WIDTH, TEX_HEIGHT
        );
    }

    private void renderSlotsAndItems(GuiGraphics context) {
        for (int visRow = 0; visRow < VISIBLE_ROWS; visRow++) {
            int row = visRow + scrollOffsetRow;
            for (int col = 0; col < COLUMNS; col++) {
                int index = row * COLUMNS + col;
                if (index < 0 || index >= stacks.size()) continue;
                ItemStack stack = stacks.get(index);
                int x = guiLeft + SLOT_OFFSET_X + col * SLOT_SIZE;
                int y = guiTop  + SLOT_OFFSET_Y + visRow * SLOT_SIZE;
                if (!stack.isEmpty()) {
                    context.renderItem(stack, x, y);
                }
            }
        }
    }

    private void drawTitle(GuiGraphics context) {
        int titleX = guiLeft + 8;
        int titleY = guiTop + 6;
        context.drawString(
                this.font,
                this.title,
                titleX,
                titleY,
                0xFF404040,
                false
        );
    }

    private void renderHoveredTooltip(GuiGraphics context, int mouseX, int mouseY) {
        int slotIndex = getSlotIndexAt(mouseX, mouseY);
        if (slotIndex < 0) return;
        if (slotIndex >= stacks.size()) return;

        ItemStack stack = stacks.get(slotIndex);
        if (stack.isEmpty()) return;

        item tempItem = main.ItemList.get(stack.getItem().toString());

        List<ClientTooltipComponent> lines = new ArrayList<>();
        Component c = Component.literal(tempItem.item_display_name).withStyle(tempItem.is_found ? ChatFormatting.GREEN : ChatFormatting.RED);
        lines.add(new ClientTextTooltip(c.getVisualOrderText()));

        context.renderTooltip(
                this.font,
                lines,
                mouseX,
                mouseY,
                DefaultTooltipPositioner.INSTANCE,
                null
        );
    }

    private int getSlotIndexAt(int mouseX, int mouseY) {
        int gridLeft   = guiLeft + SLOT_OFFSET_X;
        int gridTop    = guiTop  + SLOT_OFFSET_Y;
        int gridRight  = gridLeft + COLUMNS * SLOT_SIZE;
        int gridBottom = gridTop  + VISIBLE_ROWS * SLOT_SIZE;
        if (mouseX < gridLeft || mouseX >= gridRight || mouseY < gridTop || mouseY >= gridBottom) return -1;
        int col    = (mouseX - gridLeft) / SLOT_SIZE;
        int visRow = (mouseY - gridTop)  / SLOT_SIZE;
        int row    = visRow + scrollOffsetRow;
        int index  = row * COLUMNS + col;
        if (index < 0 || index >= stacks.size()) return -1;
        return index;
    }

    private void renderProgress(GuiGraphics context, int mouseX, int mouseY) {
        ItemStack progressStack = new ItemStack(Items.DIAMOND);

        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 3;
        int y = guiTop  + SLOT_OFFSET_Y + VISIBLE_ROWS * SLOT_SIZE + 4;

        context.renderItem(progressStack, x, y);

        int size = 16;
        if (mouseX >= x && mouseX < x + size && mouseY >= y && mouseY < y + size) {
            Component c = Component.literal("Progress: " + main.ItemList.getProgString()).withStyle(ChatFormatting.AQUA);

            List<ClientTooltipComponent> lines = new ArrayList<>();
            lines.add(new ClientTextTooltip(c.getVisualOrderText()));

            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                pendingTooltip = lines;
                pendingTooltipX = mouseX;
                pendingTooltipY = mouseY;
            }
        }
    }

    private void renderFilter(GuiGraphics context, int mouseX, int mouseY) {
        ItemStack filterStack = new ItemStack(Items.HOPPER);

        int x = guiLeft + SLOT_OFFSET_X + SLOT_SIZE * 5;
        int y = guiTop  + SLOT_OFFSET_Y + VISIBLE_ROWS * SLOT_SIZE + 4;

        context.renderItem(filterStack, x, y);

        Component c = Component.literal("Filter").withStyle(ChatFormatting.AQUA);

        List<ClientTooltipComponent> lines = new java.util.ArrayList<>();
        lines.add(new ClientTextTooltip(c.getVisualOrderText()));

        if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
            pendingTooltip = lines;
            pendingTooltipX = mouseX;
            pendingTooltipY = mouseY;
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
            onInventoryKeyPressed(Minecraft.getInstance(), !filtered);
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

    private static final ResourceLocation KNOB_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/container/creative_inventory/scroller.png");
    private static final ResourceLocation KNOB_TEXTURE_DISABLED =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/sprites/container/creative_inventory/scroller_disabled.png");

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!canScroll()) return false;

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
        if (button == 0) scrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void renderScrollbar(GuiGraphics context) {
        ResourceLocation tempTexture = canScroll() ? KNOB_TEXTURE : KNOB_TEXTURE_DISABLED;

        int x = guiLeft + SCROLLBAR_X;
        int y = guiTop  + SCROLLBAR_Y;

        context.fill(x, y, x + SCROLLBAR_WIDTH, y + SCROLLBAR_HEIGHT, 0x00202020);

        int trackHeight = SCROLLBAR_HEIGHT - KNOB_HEIGHT;
        int knobY = y + (int) (scrollPosition * trackHeight);

        context.blit(
                RenderPipelines.GUI_TEXTURED,
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