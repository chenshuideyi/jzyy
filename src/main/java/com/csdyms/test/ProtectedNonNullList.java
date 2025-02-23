package com.csdyms.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.csdy.item.sword.CsdySword;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

//public class ProtectedNonNullList extends NonNullList<ItemStack> {
//    public Inventory inventory;
//
//    public ProtectedNonNullList(NonNullList<ItemStack> old, Inventory i) {
//        super((List) (old.list instanceof DisableRemoveList ? old.list : new DisableRemoveList(old.list)), (ItemStack) old.defaultValue);
//        this.inventory = i;
//    }
//
//    public ItemStack set(int p_122795_, ItemStack p_122796_) {
//        Item item = ((ItemStack) this.list.get(p_122795_)).getItem();
//        if (item instanceof CsdySword) {
//            return p_122796_;
//        }
//
//        ItemStack re = (ItemStack) super.set(p_122795_, p_122796_);
//        EntityUtil.checkNonNullList(this, this.inventory);
//        return re;
//    }
//}

//    public ItemStack remove(int p_122793_) {
//        if (!EntityUtil.protectInventory || !(((ItemStack)this.list.get(p_122793_)).getItem() instanceof EntityEraserItem) && !(((ItemStack)this.list.get(p_122793_)).getItem() instanceof EntityProtectorItem)) {
//            ItemStack re = (ItemStack)super.remove(p_122793_);
//            EntityUtil.checkNonNullList(this, this.inventory);
//            return re;
//        } else {
//            return ItemStack.EMPTY;
//        }
//    }

//    public int size() {
//        return this.list.size();
//    }
//
////    public void clear() {
////        if (!EntityUtil.protectInventory) {
////            super.clear();
////        }
////
////        EntityUtil.checkNonNullList(this, this.inventory);
////    }
//
//    public boolean remove(Object o) {
//        if (EntityUtil.protectInventory) {
//            if (o instanceof ItemStack) {
//                ItemStack stack = (ItemStack)o;
//                if (!(stack.getItem() instanceof EntityProtectorItem) && !(stack.getItem() instanceof EntityEraserItem)) {
//                    return super.remove(o);
//                }
//            }
//
//            return false;
//        } else {
//            return super.remove(o);
//        }
//    }
//
//    public boolean removeAll(@NotNull Collection<?> c) {
//        if (EntityUtil.protectInventory) {
//            c = ((Collection)c).stream().filter((object) -> {
//                if (!(object instanceof ItemStack stack)) {
//                    return true;
//                } else {
//                    return !(stack.getItem() instanceof EntityEraserItem) && !(stack.getItem() instanceof EntityProtectorItem);
//                }
//            }).toList();
//        }
//
//        return super.removeAll((Collection)c);
//    }
//
//    public final class DisableRemoveList extends ArrayList<ItemStack> {
//    public DisableRemoveList() {
//    }
//
//    public DisableRemoveList(@NotNull Collection<? extends ItemStack> c) {
//        super(c);
//    }
//
//    public ItemStack remove(int index) {
//        ItemStack stack = (ItemStack) this.get(index);
//        return !EntityUtil.protectInventory || !(stack.getItem() instanceof CsdySword) ? (ItemStack) super.remove(index) : ItemStack.EMPTY;
//    }
//}
//
//        public boolean remove(Object o) {
//            if (EntityUtil.protectInventory) {
//                if (o instanceof ItemStack) {
//                    ItemStack stack = (ItemStack)o;
//                    if (!(stack.getItem() instanceof EntityProtectorItem) && !(stack.getItem() instanceof EntityEraserItem)) {
//                        return super.remove(o);
//                    }
//                }
//
//                return false;
//            } else {
//                return super.remove(o);
//            }
//        }
//
//        public void clear() {
//            if (!EntityUtil.protectInventory) {
//                super.clear();
//            }
//
//        }
//
//        protected void removeRange(int fromIndex, int toIndex) {
//            if (!EntityUtil.protectInventory) {
//                super.removeRange(fromIndex, toIndex);
//            }
//
//        }
//
//        public boolean removeAll(Collection<?> c) {
//            if (EntityUtil.protectInventory) {
//                c = ((Collection)c).stream().filter((object) -> {
//                    if (!(object instanceof ItemStack stack)) {
//                        return true;
//                    } else {
//                        return !(stack.getItem() instanceof EntityEraserItem) && !(stack.getItem() instanceof EntityProtectorItem);
//                    }
//                }).toList();
//            }
//
//            return super.removeAll((Collection)c);
//        }
//
//        public boolean removeIf(Predicate<? super ItemStack> filter) {
//            return super.removeIf((stack) -> {
//                if (!EntityUtil.protectInventory) {
//                    return filter.test(stack);
//                } else {
//                    return !(stack.getItem() instanceof EntityEraserItem) && !(stack.getItem() instanceof EntityProtectorItem) && filter.test(stack);
//                }
//            });
//        }
//    }
//}
