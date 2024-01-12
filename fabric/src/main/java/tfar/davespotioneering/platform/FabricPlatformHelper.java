package tfar.davespotioneering.platform;

import net.minecraft.client.gui.Gui;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.DavesPotioneeringFabric;
import tfar.davespotioneering.blockentity.*;
import tfar.davespotioneering.client.HudPreset;
import tfar.davespotioneering.duck.PlayerDuckFabric;
import tfar.davespotioneering.inv.BrewingHandlerFabric;
import tfar.davespotioneering.inv.PotionInjectorHandlerFabric;
import tfar.davespotioneering.inventory.BasicInventoryBridge;
import tfar.davespotioneering.item.CGauntletItem;
import tfar.davespotioneering.item.GauntletItemFabric;
import tfar.davespotioneering.item.UmbrellaItem;
import tfar.davespotioneering.net.C2SGauntletCyclePacket;
import tfar.davespotioneering.net.C2SPotionInjector;
import tfar.davespotioneering.net.ClientPacketHandler;
import tfar.davespotioneering.net.PacketHandler;
import tfar.davespotioneering.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Field;
import java.util.Locale;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T extends Registry<? extends F>,F> void superRegister(Class<?> clazz, T registry, Class<? extends F> filter) {
        for (Field field : clazz.getFields()) {
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    Registry.register((Registry<? super F>) registry,new ResourceLocation(DavesPotioneering.MODID,field.getName().toLowerCase(Locale.ROOT)),(F)o);
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    //loader specific instantiations

    @Override
    public Item makeBasicUmbrella(Item.Properties builder, String name, String style) {
        return new UmbrellaItem(builder,name,style);
    }

    @Override
    public Item makeAgedUmbrella(Item.Properties builder, String style) {
        return new UmbrellaItem(builder,style,style);
    }

    @Override
    public Slot makeIngSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new BrewingStandMenu.IngredientsSlot((Container) handle,slot,x,y);
    }

    @Override
    public Slot makePotSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new BrewingStandMenu.PotionSlot((Container) handle,slot,x,y);
    }

    @Override
    public Slot makeFuelSlot(BasicInventoryBridge handle, int slot, int x, int y) {
        return new BrewingStandMenu.FuelSlot((Container) handle,slot,x,y);
    }

    @Override
    public int[] getGauntletCooldowns(Player player) {
        return ((PlayerDuckFabric)player).gauntletCooldowns();
    }

    @Override
    public void setGauntletCooldowns(Player player, int[] cooldowns) {
        ((PlayerDuckFabric)player).setGauntletCooldowns(cooldowns);
    }

    //packets
    @Override
    public void syncGauntletCooldowns(Player player, int[] cooldowns) {
        PacketHandler.sendCooldowns((ServerPlayer) player,cooldowns);
    }

    @Override
    public void cycleGauntlet(boolean up) {
        C2SGauntletCyclePacket.encode(up);
    }

    @Override
    public void sendPotionInjectorButton(int id) {
        C2SPotionInjector.send(id);
    }

    @Override
    public BasicInventoryBridge makeBrewingHandler(int slots) {
        return new BrewingHandlerFabric(slots);
    }

    @Override
    public BasicInventoryBridge makePotionInjector(int slots) {
        return new PotionInjectorHandlerFabric(slots);
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
    public CPotionInjectorBlockEntity makePotionInjector(BlockPos pos, BlockState state) {
        return new PotionInjectorBlockEntity(pos,state);
    }

    @Override
    public CGauntletItem makeGauntlet(Item.Properties properties) {
        return new GauntletItemFabric(properties);
    }

    @Override
    public int rightHeight(Gui gui) {
        return 49;
    }

    @Override
    public int leftHeight(Gui gui) {
        return 49;
    }

    //configs
    @Override
    public boolean coatTools() {
        return DavesPotioneeringFabric.CONFIG.coat_tools;
    }

    @Override
    public boolean spikeFood() {
        return DavesPotioneeringFabric.CONFIG.spike_food;
    }

    @Override
    public boolean coatAnything() {
        return DavesPotioneeringFabric.CONFIG.coat_anything;
    }

    @Override
    public int coatingUses() {
        return DavesPotioneeringFabric.CONFIG.coating_uses;
    }

    @Override
    public int gauntletCooldown() {
        return DavesPotioneeringFabric.CONFIG.gauntlet_cooldown;
    }

    @Override
    public int potionSwitchCooldown() {
        return DavesPotioneeringFabric.CONFIG.potion_use_cooldown;
    }

    @Override
    public int gauntletHudX() {
        return DavesPotioneeringFabric.CONFIG.gauntlet_hud_x;
    }

    @Override
    public int gauntletHudY() {
        return DavesPotioneeringFabric.CONFIG.gauntlet_hud_y;
    }

    @Override
    public HudPreset preset() {
        return DavesPotioneeringFabric.CONFIG.gauntlet_hud_preset;
    }

    @Override
    public void setGauntletHudX(int x) {
        DavesPotioneeringFabric.CONFIG.gauntlet_hud_x = x;
    }

    @Override
    public void setGauntletHudY(int y) {
        DavesPotioneeringFabric.CONFIG.gauntlet_hud_y = y;
    }

    @Override
    public void setPreset(HudPreset preset) {
        DavesPotioneeringFabric.CONFIG.gauntlet_hud_preset = preset;
    }

    @Override
    public int particleDripRate() {
        return DavesPotioneeringFabric.CONFIG.particle_drip_rate;
    }
}
