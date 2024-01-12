package tfar.davespotioneering.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.init.ModMenuTypes;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.platform.Services;

public class CAdvancedBrewingStandMenu extends AbstractContainerMenu {
    private final ContainerData data;

    public CAdvancedBrewingStandBlockEntity blockEntity;

    //client
    public CAdvancedBrewingStandMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, Services.PLATFORM.makeBrewingHandler(CAdvancedBrewingStandBlockEntity.SLOTS), new SimpleContainerData(2),null);
    }

    //common
    public CAdvancedBrewingStandMenu(int id, Inventory playerInventory, BasicInventoryBridge inventory, ContainerData data, CAdvancedBrewingStandBlockEntity advancedBrewingStandBlockEntity) {
        super(ModMenuTypes.ADVANCED_BREWING_STAND, id);
       // assertInventorySize(inventory, 5);
       // assertIntArraySize(data, 2);
        this.data = data;

        this.blockEntity = advancedBrewingStandBlockEntity;

        int potY = 77;

        this.addSlot(Services.PLATFORM.makePotSlot(inventory, 0, 56, potY));
        this.addSlot(Services.PLATFORM.makePotSlot(inventory, 1, 79, potY + 7));
        this.addSlot(Services.PLATFORM.makePotSlot(inventory, 2, 102, potY));


        for (int i = 3; i < 3 + 4;i++) {
            this.addSlot(Services.PLATFORM.makeIngSlot(inventory, i, 22 * i - 20, 17));
        }

        int ing1 = 43;

        this.addSlot(Services.PLATFORM.makeIngSlot(inventory, 7, 79, ing1));

        this.addSlot(Services.PLATFORM.makeFuelSlot(inventory, CAdvancedBrewingStandBlockEntity.FUEL, 17, ing1));

        this.addDataSlots(data);

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

    /**
     * Determines whether supplied player can use this container
     */
    public boolean stillValid(Player playerIn) {
        return true;//this.tileBrewingStand.isUsableByPlayer(playerIn);
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            if (index < CAdvancedBrewingStandBlockEntity.SLOTS) {
                if (!this.moveItemStackTo(slotStack, CAdvancedBrewingStandBlockEntity.SLOTS, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, CAdvancedBrewingStandBlockEntity.SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemstack);
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return itemstack;
    }

    /**
     * Merges provided ItemStack with the first available one in the container/player inventor between minIndex
     * (included) and maxIndex (excluded). Args : stack, minIndex, maxIndex, negativDirection. [!] the Container
     * implementation do not check if the item is valid for the slot
     *
     */
    protected boolean moveItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        if (DavesPotioneering.isFabric) {
            return patchedMoveTo(this,pStack,pStartIndex,pEndIndex,pReverseDirection);
        }
        //don't use the patch on forge
        return super.moveItemStackTo(pStack,pStartIndex,pEndIndex,pReverseDirection);
    }


    /**
     *
     *!!!this is required on fabric, but not forge
     *
     * @param menu
     * @param pStack
     * @param pStartIndex
     * @param pEndIndex
     * @param pReverseDirection
     * @return
     */
    public static boolean patchedMoveTo(AbstractContainerMenu menu,ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
        boolean flag = false;
        int i = pStartIndex;
        if (pReverseDirection) {
            i = pEndIndex - 1;
        }

        if (pStack.isStackable()) {
            while(!pStack.isEmpty()) {
                if (pReverseDirection) {
                    if (i < pStartIndex) {
                        break;
                    }
                } else if (i >= pEndIndex) {
                    break;
                }

                Slot slot = menu.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(pStack, itemstack)) {
                    int j = itemstack.getCount() + pStack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), pStack.getMaxStackSize());
                    if (j <= maxSize) {
                        pStack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        pStack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (pReverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!pStack.isEmpty()) {
            if (pReverseDirection) {
                i = pEndIndex - 1;
            } else {
                i = pStartIndex;
            }

            while(true) {
                if (pReverseDirection) {
                    if (i < pStartIndex) {
                        break;
                    }
                } else if (i >= pEndIndex) {
                    break;
                }

                Slot slot1 = menu.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(pStack)) {
                    if (pStack.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(pStack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(pStack.split(pStack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (pReverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    public int getFuel() {
        return this.data.get(CAdvancedBrewingStandBlockEntity.DATA_FUEL_USES);
    }

    public int getBrewTime() {
        return this.data.get(CAdvancedBrewingStandBlockEntity.DATA_BREW_TIME);
    }

}
