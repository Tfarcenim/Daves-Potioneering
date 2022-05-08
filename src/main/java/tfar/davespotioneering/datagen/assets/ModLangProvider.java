package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.davespotioneering.DavesPotioneering;
import tfar.davespotioneering.block.PotionInjectorBlock;
import tfar.davespotioneering.client.GauntletHUD;
import tfar.davespotioneering.client.GauntletHUDMovementGui;
import tfar.davespotioneering.init.ModBlocks;
import tfar.davespotioneering.init.ModEffects;
import tfar.davespotioneering.init.ModItems;
import tfar.davespotioneering.init.ModPotions;
import tfar.davespotioneering.item.UmbrellaItem;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(DataGenerator gen) {
        super(gen, DavesPotioneering.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        addEffect(() -> ModEffects.MILK,"Milk");
        addPotions();
        add("container.davespotioneering.compound_brewing","Compound Brewing Stand");
        add(PotionInjectorBlock.TRANS_KEY,"Potion Injector");
        addBlock(() -> ModBlocks.COMPOUND_BREWING_STAND,"Compound Brewing Stand");
        addBlock(() -> ModBlocks.REINFORCED_CAULDRON,"Reinforced Cauldron");
        addBlock(() -> ModBlocks.POTION_INJECTOR,"Potion Injector");
        addItem(() -> ModItems.POTIONEER_GAUNTLET,getNameFromItem(ModItems.POTIONEER_GAUNTLET));
        addItem(() -> ModItems.RUDIMENTARY_GAUNTLET,getNameFromItem(ModItems.RUDIMENTARY_GAUNTLET));
        addItem(() -> ModItems.NETHERITE_GAUNTLET,getNameFromItem(ModItems.NETHERITE_GAUNTLET));

        add("davespotioneering.gui.moveGauntletHUD", "Use your mouse to drag the Gauntlet HUD wherever you would like or use one of these default positions.");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.TOP_LEFT.ordinal(), "Left Top");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.TOP_RIGHT.ordinal(), "Right Top");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.BTM_LEFT.ordinal(), "Left Bottom");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.BTM_RIGHT.ordinal(), "Right Bottom");
        add(GauntletHUDMovementGui.KEY+ GauntletHUD.HudPresets.ABOVE_HOTBAR.ordinal(), "Above Hotbar");
        add("davespotioneering.tooltip.gauntlet", "%s %s");
        add("davespotioneering.tooltip.gauntlet.withDuration", "%s %s (%s)");
        addGroup(ModItems.tab,"Dave's Potioneering");

        ModItems.getAllItems().stream().filter(UmbrellaItem.class::isInstance)
                .forEach(item -> {
                    addItem(() -> item, "Umbrella");
                    addDesc(item,"Keeps you from getting wet!");
                });
        addShiftDesc(ModBlocks.COMPOUND_BREWING_STAND,"An upgraded stand that eases the tedium of brewing.");
        addHoldSDesc(ModBlocks.COMPOUND_BREWING_STAND,"Summary: Hold [Shift]");
        addCtrlDescs(ModBlocks.COMPOUND_BREWING_STAND,"- Potions brew at 2x speed",
                "- More ingredient slots",
                "- Double potion output (6 potions)");
        addHoldCDesc(ModBlocks.COMPOUND_BREWING_STAND,"Features: Hold [CTRL]");


        addHoldSDesc(ModBlocks.REINFORCED_CAULDRON,"Summary: Hold [Shift]");
        addShiftDescs(ModBlocks.REINFORCED_CAULDRON,"An upgraded cauldron that enables the coating of melee weapons in potion effects.",
                "It also has some small benefits.");

        addHoldCDesc(ModBlocks.REINFORCED_CAULDRON,"Features: Hold [CTRL]");
        addCtrlDescs(ModBlocks.REINFORCED_CAULDRON,"- Water is not depleted when filling empty bottles",
                "- Can be filled with potions and Milk");

        addHoldADesc(ModBlocks.REINFORCED_CAULDRON,"Coating: Hold [Alt]");
        addAltDescs(ModBlocks.REINFORCED_CAULDRON,"Fill the cauldron with any potion and Dragon's Breath.",
                        "Then, toss your weapon of choice into the concoction.");


        addShiftDesc(ModItems.POTIONEER_GAUNTLET,"An alchemical weapon that utilizes potions and brute force in a Netherite knuckle sandwich!");
        addHoldSDesc(ModItems.POTIONEER_GAUNTLET,"Summary: Hold [Shift]");
        addCtrlDesc(ModItems.POTIONEER_GAUNTLET,"Shift+RMB - Ignites or extinguishes the internal blaze. (toggles potion usage)\n" +
                "Shift+Scroll wheel up/down - cycles through one of six potions injected into the gauntlet.");
        addHoldCDesc(ModItems.POTIONEER_GAUNTLET,"Controls: Hold [Ctrl]");


        addShiftDesc(ModBlocks.POTION_INJECTOR,"A workstation necessary for preparing the Potioneer Gauntlet.");
        addHoldSDesc(ModBlocks.POTION_INJECTOR,"Summary: Hold [Shift]");
        addCtrlDesc(ModBlocks.POTION_INJECTOR,"Use this block to inject Blaze Powder and Lingering Potions into the Potioneer Gauntlet.");
        addHoldCDesc(ModBlocks.POTION_INJECTOR,"Functionality: Hold [Ctrl]");

        addConfig("gauntlet_hud_x","The X Position of the gauntlet hud (left top). You should be using the in-game gui to change this though");
        addConfig("gauntlet_hud_y","The y Position of the gauntlet hud (left top). You should be using the in-game gui to change this though");
        addConfig("gauntlet_hud_preset","You shouldn't change this. Just don't");
    }

    protected void addConfig(String value,String trans) {
        add("config."+value,trans);
    }

    protected void addDesc(IItemProvider item, String desc) {
        add(item.asItem().getTranslationKey()+".desc",desc);
    }

    protected void addShiftDesc(IItemProvider item, String desc) {
        add(item.asItem().getTranslationKey()+".shift.desc",desc);
    }

    protected void addShiftDescs(IItemProvider item, String... descs) {
        for (int i = 0; i < descs.length; i++) {
            String desc = descs[i];
            add(item.asItem().getTranslationKey() + i+".shift.desc", desc);
        }
    }

    protected void addCtrlDesc(IItemProvider item, String desc) {
        add(item.asItem().getTranslationKey()+".ctrl.desc",desc);
    }

    protected void addCtrlDescs(IItemProvider item, String... descs) {
        for (int i = 0; i < descs.length; i++) {
            String desc = descs[i];
            add(item.asItem().getTranslationKey() + i+".ctrl.desc", desc);
        }
    }

    protected void addAltDesc(IItemProvider item, String desc) {
        add(item.asItem().getTranslationKey()+".alt.desc",desc);
    }

    protected void addAltDescs(IItemProvider item, String... descs) {
        for (int i = 0; i < descs.length; i++) {
            String desc = descs[i];
            add(item.asItem().getTranslationKey() + i+".alt.desc", desc);
        }
    }

    protected void addHoldSDesc(IItemProvider item, String desc) {
        add(item.asItem().getTranslationKey()+".hold_shift.desc",desc);
    }

    protected void addHoldCDesc(IItemProvider item, String desc) {
        add(item.asItem().getTranslationKey()+".hold_ctrl.desc",desc);
    }

    protected void addHoldADesc(IItemProvider item, String desc) {
        add(item.asItem().getTranslationKey()+".hold_alt.desc",desc);
    }

    protected void addGroup(ItemGroup group,String name) {
        add(group.getGroupName().getString(),name);
    }

    public void addPotions() {
        add(ModPotions.MILK.getNamePrefixed(Items.POTION.getTranslationKey() + ".effect."),"Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.SPLASH_POTION.getTranslationKey() + ".effect."),"Splash Milk Bottle");
        add(ModPotions.MILK.getNamePrefixed(Items.LINGERING_POTION.getTranslationKey() + ".effect."),"Lingering Milk Bottle");
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getTranslationKey().split("\\.")[2].replace("_", " "));
    }
}
