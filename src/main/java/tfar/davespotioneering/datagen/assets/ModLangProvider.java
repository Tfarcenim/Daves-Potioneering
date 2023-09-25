package tfar.davespotioneering.datagen.assets;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
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
        addBlock(() -> ModBlocks.REINFORCED_WATER_CAULDRON,"Reinforced Water Cauldron");
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
        addGroup(ModItems.tab,"Dave's Potioneering");

        ModItems.getAllItems().stream().filter(UmbrellaItem.class::isInstance)
                .forEach(item -> {
                    addItem(() -> item, "Umbrella");
                    addDesc(item,"Keeps you from getting wet!");
                });
        detailedDescriptions();
    }

    public void detailedDescriptions() {
        addShiftDesc(ModBlocks.COMPOUND_BREWING_STAND,"An upgraded stand that eases the tedium of brewing.");
        addHoldSDesc(ModBlocks.COMPOUND_BREWING_STAND,"Summary: Hold [Shift]");
        addCtrlDescs(ModBlocks.COMPOUND_BREWING_STAND,"- Potions brew at 2x speed",
                "- More ingredient slots",
                "- Double potion output (6 potions)");
        addHoldCDesc(ModBlocks.COMPOUND_BREWING_STAND,"Features: Hold [CTRL]");


        addHoldSDesc(ModBlocks.REINFORCED_CAULDRON,"Summary: Hold [Shift]");
        addShiftDesc(ModBlocks.REINFORCED_CAULDRON,"A new cauldron that has some mechanical differences/benefits over the Vanilla one.");

        addHoldCDesc(ModBlocks.REINFORCED_CAULDRON,"Features: Hold [CTRL]");
        addCtrlDescs(ModBlocks.REINFORCED_CAULDRON,
                "- Water is not depleted when filling empty bottles.",
                "- Can be filled with potions. Don't mix different flavors!",
                "- Can be filled with milk"
        );

        addHoldADesc(ModBlocks.REINFORCED_CAULDRON,"Coating: Hold [Alt]");
        addAltDescs(ModBlocks.REINFORCED_CAULDRON,"1. Once the Reinforced Cauldron is filled with 3 similar potions and Dragon's Breath, a mixture is made.",
                "2. Next, throw the item/weapon/tool/arrows you would like to coat into the cauldron.",
                "3. The liquid will sizzle and evaporate until there is nothing left but the newly coated item.");


        addShiftDesc(ModItems.POTIONEER_GAUNTLET,"An alchemical weapon that utilizes potions and brute force in a Netherite knuckle sandwich!");
        addHoldSDesc(ModItems.POTIONEER_GAUNTLET,"Summary: Hold [Shift]");
        addHoldCDesc(ModItems.POTIONEER_GAUNTLET,"Controls: Hold [Ctrl]");
        addCtrlDescs(ModItems.POTIONEER_GAUNTLET,"Shift+RMB - Ignites or extinguishes the internal blaze. (toggles potion usage)",
                "Shift+Scroll wheel up/down - cycles through one of six potions injected into the gauntlet.",
                "Shift+Middle Mouse Button - customize Gauntlet HUD");


        addShiftDesc(ModBlocks.POTION_INJECTOR,"A workstation necessary for preparing the Potioneer Gauntlet.");
        addHoldSDesc(ModBlocks.POTION_INJECTOR,"Summary: Hold [Shift]");
        addCtrlDesc(ModBlocks.POTION_INJECTOR,"Use this block to inject Blaze Powder and Lingering Potions into the Potioneer Gauntlet.");
        addHoldCDesc(ModBlocks.POTION_INJECTOR,"Functionality: Hold [Ctrl]");
        addTooltip("coated_with","Coated with:");
        addTooltip("spiked_with","Spiked with:");
        add("key.davespotioneering.open_config","Open Config");
        add("key.categories."+DavesPotioneering.MODID,"Dave's Potioneering");
        addConfig("gauntlet_hud_x","The X Position of the gauntlet hud (left top). You should be using the in-game gui to change this though");
        addConfig("gauntlet_hud_y","The y Position of the gauntlet hud (left top). You should be using the in-game gui to change this though");
        addConfig("gauntlet_hud_preset","You shouldn't change this. Just don't");
    }
    protected void addConfig(String value,String trans) {
        add("config."+value,trans);
    }
    protected void addTooltip(String code,String tip) {
        add(DavesPotioneering.MODID+"."+code+"."+"tooltip",tip);
    }
    protected void addDesc(ItemLike item, String desc) {
        add(item.asItem().getDescriptionId()+".desc",desc);
    }

    protected void addShiftDesc(ItemLike item, String desc) {
        add(item.asItem().getDescriptionId()+".shift.desc",desc);
    }

    protected void addShiftDescs(ItemLike item, String... descs) {
        for (int i = 0; i < descs.length; i++) {
            String desc = descs[i];
            add(item.asItem().getDescriptionId() + i+".shift.desc", desc);
        }
    }

    protected void addCtrlDesc(ItemLike item, String desc) {
        add(item.asItem().getDescriptionId()+".ctrl.desc",desc);
    }

    protected void addCtrlDescs(ItemLike item, String... descs) {
        for (int i = 0; i < descs.length; i++) {
            String desc = descs[i];
            add(item.asItem().getDescriptionId() + i+".ctrl.desc", desc);
        }
    }

    protected void addAltDesc(ItemLike item, String desc) {
        add(item.asItem().getDescriptionId()+".alt.desc",desc);
    }

    protected void addAltDescs(ItemLike item, String... descs) {
        for (int i = 0; i < descs.length; i++) {
            String desc = descs[i];
            add(item.asItem().getDescriptionId() + i+".alt.desc", desc);
        }
    }

    protected void addHoldSDesc(ItemLike item, String desc) {
        add(item.asItem().getDescriptionId()+".hold_shift.desc",desc);
    }

    protected void addHoldCDesc(ItemLike item, String desc) {
        add(item.asItem().getDescriptionId()+".hold_ctrl.desc",desc);
    }

    protected void addHoldADesc(ItemLike item, String desc) {
        add(item.asItem().getDescriptionId()+".hold_alt.desc",desc);
    }

    protected void addGroup(CreativeModeTab group,String name) {
        add(group.getDisplayName().getString(),name);
    }

    public void addPotions() {
        add(ModPotions.MILK.getName(Items.POTION.getDescriptionId() + ".effect."),"Milk Bottle");
        add(ModPotions.MILK.getName(Items.SPLASH_POTION.getDescriptionId() + ".effect."),"Splash Milk Bottle");
        add(ModPotions.MILK.getName(Items.LINGERING_POTION.getDescriptionId() + ".effect."),"Lingering Milk Bottle");
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }
}
