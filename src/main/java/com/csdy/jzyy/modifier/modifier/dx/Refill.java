package com.csdy.jzyy.modifier.modifier.dx;

/*
public class Refill extends NoLevelsModifier implements MeleeHitModifierHook, OnAttackedModifierHook {
    private static final Random random = new Random();

    @Override
    public void onAttacked(IToolStackView tool, ModifierEntry entry, EquipmentContext context, EquipmentSlot slot, DamageSource damageSource, float amount, boolean isDirectDamage) {
        if (!(context.getEntity() instanceof Player player)) return;

        if (random.nextFloat() <= 0.02f) {
            ItemStack cap = new ItemStack(ItemRegister.MOZHUA_CAP.get());

            player.level().addFreshEntity(new ItemEntity(
                    player.level(),
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    cap.copy()
            ));

        }
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        LivingEntity target = context.getLivingTarget();
        Player player = context.getPlayerAttacker();
        if (target != null && player != null && context.isCritical()) {

            if (random.nextFloat() <= 0.02f) {
                ItemStack cap = new ItemStack(ItemRegister.MOZHUA_CAP.get());

                player.level().addFreshEntity(new ItemEntity(
                        player.level(),
                        player.getX(),
                        player.getY() + 0.5,
                        player.getZ(),
                        cap.copy()
                ));

            }
        }

    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.ON_ATTACKED);
        hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
    }

}
*/