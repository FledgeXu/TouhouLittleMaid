package com.github.tartaricacid.touhoulittlemaid.inventory;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class MaidInventoryContainer extends MaidMainContainer {
    private int startRow;

    public MaidInventoryContainer(IInventory playerInventory, EntityMaid maid, int taskIndex, int startRow) {
        super(playerInventory, maid, taskIndex);
        this.startRow = MathHelper.clamp(startRow, 0, maid.getBackLevel().getLevel() * 2);
        addMaidInventorySlots();
    }

    private void addMaidInventorySlots() {
        IItemHandler itemHandler = maid.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        // 女仆物品栏
        for (int l = 0; l < 3; ++l) {
            for (int j = 0; j < 5; ++j) {
                this.addSlotToContainer(new SlotItemHandler(itemHandler, 6 + j + (l + startRow) * 5, 80 + j * 18, 8 + l * 18));
            }
        }
    }

    /**
     * 处理 Shift 点击情况下的物品逻辑
     */
    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < 42) {
                if (!this.mergeItemStack(itemstack1, 42, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 42, true)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}