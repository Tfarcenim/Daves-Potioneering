package tfar.davespotioneering.menu;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import tfar.davespotioneering.init.ModContainerTypes;
import tfar.davespotioneering.inv.SingleItemHandler;

public class GauntletMenu extends Container {

    //client
    public GauntletMenu(int id, PlayerInventory playerInventory) {
        this(id, playerInventory, new SingleItemHandler(6));
    }

    //common
    public GauntletMenu(int id, PlayerInventory playerInventory, ItemStackHandler inventory) {
        super(ModContainerTypes.ALCHEMICAL_GAUNTLET, id);
        // assertInventorySize(inventory, 5);
        // assertIntArraySize(data, 2);

        int potY = 77;

        for (int i = 0; i < 6;i++) {
            this.addSlot(new SlotItemHandler(inventory, i, 22 * i - 20, 17));
        }

        int invX = 8;
        int invY = 110;

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, invX + j * 18, invY + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, invX + k * 18, invY + 58));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
