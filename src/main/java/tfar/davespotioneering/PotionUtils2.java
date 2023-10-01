package tfar.davespotioneering;

        import com.google.common.collect.Lists;
        import com.mojang.datafixers.util.Pair;
        import net.minecraft.ChatFormatting;
        import net.minecraft.core.registries.BuiltInRegistries;
        import net.minecraft.nbt.CompoundTag;
        import net.minecraft.nbt.ListTag;
        import net.minecraft.network.chat.CommonComponents;
        import net.minecraft.network.chat.Component;
        import net.minecraft.network.chat.MutableComponent;
        import net.minecraft.world.effect.MobEffect;
        import net.minecraft.world.effect.MobEffectInstance;
        import net.minecraft.world.effect.MobEffectUtil;
        import net.minecraft.world.entity.ai.attributes.Attribute;
        import net.minecraft.world.entity.ai.attributes.AttributeModifier;
        import net.minecraft.world.item.ItemStack;
        import net.minecraft.world.item.alchemy.Potion;
        import net.minecraft.world.item.alchemy.PotionUtils;

        import java.util.Collection;
        import java.util.List;
        import java.util.Map;

public class PotionUtils2 extends PotionUtils {


    private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

    public static CompoundTag saveAllEffects(CompoundTag tag, Potion potion, List<MobEffectInstance> customEffects, Integer color) {
        saveAllEffects(tag, potion, customEffects);
        if (color != null) {
            saveColor(tag,color);
        }
        return tag;
    }

    public static CompoundTag saveAllEffects(CompoundTag tag, Potion potion, List<MobEffectInstance> customEffects) {
        savePotion(tag,potion);
        saveCustomEffects(tag,customEffects);
        return tag;
    }

    private static CompoundTag savePotion(CompoundTag tag, Potion potion) {
        tag.putString(TAG_POTION, BuiltInRegistries.POTION.getKey(potion).toString());
        return tag;
    }

    private static CompoundTag saveCustomEffects(CompoundTag tag, Collection<MobEffectInstance> pEffects) {
        if (!pEffects.isEmpty()) {
            ListTag listtag = tag.getList(TAG_CUSTOM_POTION_EFFECTS, 9);
            for (MobEffectInstance mobeffectinstance : pEffects) {
                listtag.add(mobeffectinstance.save(new CompoundTag()));
            }
            tag.put(TAG_CUSTOM_POTION_EFFECTS, listtag);
        }
        return tag;
    }

    public static ItemStack setCustomColor(ItemStack stack,int color) {
        stack.getOrCreateTag().putInt(TAG_CUSTOM_POTION_COLOR,color);
        return stack;
    }

    private static CompoundTag saveColor(CompoundTag tag, int color) {
        tag.putInt(TAG_CUSTOM_POTION_COLOR,color);
        return tag;
    }

    /**
     * Adds the tooltip of the {@code Potion} stored on the {@code ItemStack} along a "durationFactor"
     * @param potionTag the passed {@code CompoundTag}
     * @param pTooltips the passed list of current {@code Component} tooltips
     * @param pDurationFactor the passed durationFactor of the {@code Potion}
     */
    public static void addPotionTooltip(CompoundTag potionTag, List<Component> pTooltips, float pDurationFactor) {
        List<MobEffectInstance> list = getAllEffects(potionTag);
        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (list.isEmpty()) {
            pTooltips.add(NO_EFFECT);
        } else {
            for(MobEffectInstance mobeffectinstance : list) {
                MutableComponent mutablecomponent = Component.translatable(mobeffectinstance.getDescriptionId());
                MobEffect mobeffect = mobeffectinstance.getEffect();
                Map<Attribute, AttributeModifier> map = mobeffect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), mobeffect.getAttributeModifierValue(mobeffectinstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (mobeffectinstance.getAmplifier() > 0) {
                    mutablecomponent = Component.translatable("potion.withAmplifier", mutablecomponent, Component.translatable("potion.potency." + mobeffectinstance.getAmplifier()));
                }

                if (mobeffectinstance.getDuration() > 20) {
                    mutablecomponent = Component.translatable("potion.withDuration", mutablecomponent, MobEffectUtil.formatDuration(mobeffectinstance, pDurationFactor));
                }

                pTooltips.add(mutablecomponent.withStyle(mobeffect.getCategory().getTooltipFormatting()));
            }
        }

        if (!list1.isEmpty()) {
            pTooltips.add(CommonComponents.EMPTY);
            pTooltips.add((Component.translatable("potion.whenDrank")).withStyle(ChatFormatting.DARK_PURPLE));

            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    pTooltips.add((Component.translatable("attribute.modifier.plus." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId()))).withStyle(ChatFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 *= -1.0D;
                    pTooltips.add((Component.translatable("attribute.modifier.take." + attributemodifier2.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1), Component.translatable(pair.getFirst().getDescriptionId()))).withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}