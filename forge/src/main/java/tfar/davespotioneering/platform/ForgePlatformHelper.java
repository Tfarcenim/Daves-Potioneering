package tfar.davespotioneering.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringForge;
import tfar.davespotioneering.ModConfig;
import tfar.davespotioneering.blockentity.AdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.CAdvancedBrewingStandBlockEntity;
import tfar.davespotioneering.blockentity.CReinforcedCauldronBlockEntity;
import tfar.davespotioneering.blockentity.ReinforcedCauldronBlockEntity;
import tfar.davespotioneering.inv.BrewingHandler;
import tfar.davespotioneering.inv.PotionInjectorHandler;
import tfar.davespotioneering.inv.slots.FuelSlot;
import tfar.davespotioneering.inv.slots.IngredientSlot;
import tfar.davespotioneering.inv.slots.PotionSlot;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.AgedUmbrellaItem;
import tfar.davespotioneering.item.CGauntletItem;
import tfar.davespotioneering.item.GauntletItem;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.net.PacketHandler;
import tfar.davespotioneering.net.S2CCooldownPacket;
import tfar.davespotioneering.platform.services.IPlatformHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public <T> void superRegister(Class<?> clazz, Registry<T> registry, Class<? extends T> filter) {
            List<Pair<ResourceLocation, Supplier<?>>> list = DavesPotioneeringForge.registerLater.computeIfAbsent(registry, k -> new ArrayList<>());
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    list.add(Pair.of(new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),() -> o));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    @Override
    public Item makeBasicUmbrella(Item.Properties builder, String name, String style) {
        return new UmbrellaItem(builder,name,style);
    }

    @Override
    public Item makeAgedUmbrella(Item.Properties builder, String style) {
        return new AgedUmbrellaItem(builder,style);
    }

    public Slot makeBasic(BasicInventoryBridge handle, int slot, int x, int y) {
        return new SlotItemHandler((IItemHandler) handle,slot,x,y);
    }
    @Override
    public Slot makeIngSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new IngredientSlot((IItemHandler) handle,slot,x,y);
    }

    @Override
    public Slot makePotSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new PotionSlot((IItemHandler) handle,slot,x,y);
    }

    @Override
    public Slot makeFuelSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new FuelSlot((IItemHandler) handle,slot,x,y);
    }

    @Override
    public BasicInventoryBridge makeBrewingHandler(int slots) {
        return new BrewingHandler(slots);
    }

    @Override
    public BasicInventoryBridge makePotionInjector(int slots) {
        return new PotionInjectorHandler(slots);
    }

    @Override
    public CAdvancedBrewingStandBlockEntity makeAdvancedBrewingStand(BlockPos pos, BlockState state) {
        return new AdvancedBrewingStandBlockEntity(pos,state);
    }

    @Override
    public CReinforcedCauldronBlockEntity makeReinforcedCauldron(BlockPos pos, BlockState state) {
        return new ReinforcedCauldronBlockEntity(pos,state);
    }

    @Override
    public int[] getGauntletCooldowns(Player player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(DavesPotioneering.MODID)) return new int[6];
        CompoundTag tag = persistent.getCompound(DavesPotioneering.MODID);
        int[] cooldowns = tag.getIntArray(CGauntletItem.COOLDOWNS);
        return cooldowns;
    }

    @Override
    public void setGauntletCooldowns(Player player, int[] cooldowns) {
        CompoundTag persistent = player.getPersistentData();
        CompoundTag tag = persistent.getCompound(DavesPotioneering.MODID);
        tag.putIntArray(CGauntletItem.COOLDOWNS, cooldowns);
    }

    @Override
    public void syncGauntletCooldowns(Player player, int[] cooldowns) {
        PacketHandler.sendToClient(new S2CCooldownPacket(cooldowns), (ServerPlayer) player);
    }

    @Override
    public CGauntletItem makeGauntlet(Item.Properties properties) {
        return new GauntletItem(properties);
    }

    //configs

    @Override
    public boolean coatTools() {
        return ModConfig.Server.coat_tools.get();
    }

    @Override
    public boolean spikeFood() {
        return ModConfig.Server.spike_food.get();
    }

    @Override
    public boolean coatAnything() {
        return ModConfig.Server.coat_anything.get();
    }

    @Override
    public int coatingUses() {
        return ModConfig.Server.coating_uses.get();
    }

    @Override
    public int gauntletCooldown() {
        return ModConfig.Server.gauntlet_cooldown.get();
    }
}