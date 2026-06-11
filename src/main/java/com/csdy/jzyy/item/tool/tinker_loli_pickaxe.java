package com.csdy.jzyy.item.tool;


//public class tinker_loli_pickaxe extends ModifiableItem {
//    public tinker_loli_pickaxe(Properties properties) {
//        super(properties, ToolDefinitions.TINKER_LOLI_PICKAXE_TD);
//    }
//
//    private static final double baseRange = 10;
//
//    @Override
//    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity living) {
//        return true;
//    }
//
//    public float getDestroySpeed(@NotNull ItemStack stack, @NotNull BlockState state) {
//        ToolStack tool=ToolStack.from(stack);
//        return tool.getStats().get(ToolStats.MINING_SPEED);
//    }
//
//
//    @Override
//    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
//        return true;
//    }
//
//    public List<Component> getStatInformation(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
//        tooltips = this.getTooltipStats(tool, player, tooltips, key, tooltipFlag);
//        return tooltips;
//    }
//    public List<Component> getTooltipStats(IToolStackView tool, @Nullable Player player, List<Component> tooltips, TooltipKey key, TooltipFlag tooltipFlag) {
//        TooltipBuilder builder = new TooltipBuilder(tool, tooltips);
//        if (tool.hasTag(TinkerTags.Items.DURABILITY)) {
//            builder.add(ToolStats.DURABILITY);
//        }
//        if (tool.hasTag(TinkerTags.Items.MELEE)) {
//            builder.add(ToolStats.ATTACK_DAMAGE);
//            builder.add(ToolStats.ATTACK_SPEED);
//        }
//        if (tool.hasTag(TinkerTags.Items.HARVEST)) {
//            builder.add(ToolStats.HARVEST_TIER);
//            builder.add(ToolStats.MINING_SPEED);
//        }
//        builder.addAllFreeSlots();
//        Iterator var7 = tool.getModifierList().iterator();
//        while(var7.hasNext()) {
//            ModifierEntry entry = (ModifierEntry)var7.next();
//            entry.getHook(ModifierHooks.TOOLTIP).addTooltip(tool, entry, player, tooltips, key, tooltipFlag);
//        }
//        return tooltips;
//    }
//
//    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
//        InteractionResultHolder<ItemStack> use = super.use(level, player, hand);
//        int entityCount = 0;
//        int itemCount = 0;
//        int xpCount = 0;
//
//        ItemStack stack = player.getItemInHand(hand);
//        ToolStack tool = ToolStack.from(stack);
//        int expandedLevel = tool.getModifierLevel(TinkerModifiers.expanded.getId());
//
//        double range = Math.min(baseRange + 10 * expandedLevel, 100);
//        double rangeSq = range * range;
//
//        if (level instanceof ServerLevel serverLevel && !level.isClientSide) {
//
//            double playerX = player.getX();
//            double playerY = player.getY();
//            double playerZ = player.getZ();
//
//            AABB searchArea = new AABB(playerX - range, playerY - range, playerZ - range,
//                    playerX + range, playerY + range, playerZ + range);
//
//            for (ItemEntity item : serverLevel.getEntitiesOfClass(ItemEntity.class, searchArea, e ->
//                    !e.isRemoved() && e.distanceToSqr(player) <= rangeSq)) {
//                item.setPos(playerX, playerY, playerZ);
//                itemCount++;
//            }
//
//            for (ExperienceOrb xpOrb : serverLevel.getEntitiesOfClass(ExperienceOrb.class, searchArea, e ->
//                    !e.isRemoved() && e.distanceToSqr(player) <= rangeSq)) {
//                xpOrb.setPos(playerX, playerY, playerZ);
//                xpCount++;
//            }
//
//            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, searchArea, e ->
//                    e != null && e.isAlive() && !(e instanceof Player) && e.distanceToSqr(player) <= rangeSq)) {
//
//                // 添加安全检查，防止递归
//                if (!entity.isRemoved() && !entity.isDeadOrDying()) {
//                    ToolAttackUtil.attackEntity(stack, player, entity);
//                    entityCount++;
//                }
//            }
//
//            if (entityCount > 0 || itemCount > 0 || xpCount > 0) {
//                player.displayClientMessage(Component.literal("已攻击" + entityCount + "个生物，吸引" + itemCount + "个物品和" + xpCount + "个经验球"), false);
//            }
//
//        }
//
//        player.playSound(SoundsRegister.LOLI_SUCCRSS.get(), 1, 1);
//        return use;
//    }
//
//    @Override
//    public boolean isDamageable(ItemStack stack) {
//        return false;
//    }
//
//}
