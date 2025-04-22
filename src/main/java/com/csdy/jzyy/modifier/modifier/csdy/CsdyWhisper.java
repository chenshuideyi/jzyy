package com.csdy.jzyy.modifier.modifier.csdy;

import com.csdy.jzyy.font.RainbowText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Random;

public class CsdyWhisper extends NoLevelsModifier implements InventoryTickModifierHook {

//    String[] array = {
//            "我会在玫瑰花送达之前泡进浴缸",
//            "对着QQ号思春吧",
//            "我是一个凄美的结尾",
//            "爱我然后活过来，这是21世纪魔法",
//            "承认你狗的身份然后心安理得的幸福生活下去",
//            "我爱我自己，我恨我自己",
//            "质子怎么承担我的重量",
//            "把我跳跃到天空时自傲的神情传送",
//            "痛苦空虚协调凌乱调节反反复复一次一次",
//            "要是从来没有和你相遇过就好了这样默念道",
//            "你把事情端出来说太恶心了",
//            "我是你的一次性玩具吗",
//            "人和人都是偷情的傻逼",
//            "你始终认为我是疯子",
//            "永世隔绝的理想乡",
//            "空气从肺里跑出来了就好了",
//            "让咱们走吧，就你还有我",
//            "当十一日帝国正将天空吞没",
//            "宛如人形溶烂得像蛤蜊摆上早餐桌",
//            "只是想着关于你的事也是一种任性吗",
//            "停止停止停止停止停止停止停止停止停止停止停止",
//            "世界是方的，而我们都是试图把自己塞进圆形孔洞里的尖叫鸡",
//            "不能够，不能够",
//            "hug me",
//            "我好想你",
//            "一万万又一万万又一万万万年",
//            "那么我是，败犬吗"
//    };

    static Random random = new Random();

    private static final String COMMON_CHINESE_CHARACTERS =
            "的一是在不了有和人这中大为上个国我以要他时来用们生到作地于出就分对成会可主发年动同工也能下过子说产种面而方后多定行学法所民得经十三之进" +
            "着等部度家电力里如水化高自二理起小物现实加量都两体制机当使点从业本去把性好应开它合还因由其些然前外天政四日那社义事平形相全表间样" +
            "与关各重新线内数正心反你明看原又么利比或但质气第向道命此变条只没结解问意建月公无系军很情者最立代想已通并提直题党程展五果料象员革位" +
            "入常文总次品式活设及管特件长求老头资边流路级少图山统接知较将组见计别她手角期根论运农指几九区强放决西被干做必战先回则任取据处队南给色" +
            "光门即保治北造百规热领七海口东导器压志世金增争济阶油思术极交受联什认六共权收证改清己美再采转更单风切打白教速花带安场身车例真务具万" +
            "每目至达走积示议声报斗完类八离华名确才科张信马节话整空元况今集温传土许步群广石记需段研界拉林律尖叫鸡且究观越织装影算低持音众书布复容儿" +
            "须际商非验连断深难近矿千周委素技备半办青省列习响约支般史感劳便团往酸历市克何除消构府称太准精值号率族维划选标写存候毛亲快效斯院查江型" +
            "眼王按格养易置派层片始却专状育厂京识适属圆包火住调满县局照参红细引听该铁价严龙飞椒淑耄耋哈基米";


    public static String generateRandomSentence() {
        int length = 4 + random.nextInt(17);
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(COMMON_CHINESE_CHARACTERS.charAt(random.nextInt(COMMON_CHINESE_CHARACTERS.length())));
        }

        return sb.toString();
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!(holder instanceof Player player)) return;
        if(player.getCommandSenderWorld().isClientSide) return;
        if (player.tickCount % 2 != 0) return;
//        int randomIndex = random.nextInt(array.length);
//        String message = array[randomIndex];
        String coloredMessage = RainbowText.makeColour(generateRandomSentence());
        player.displayClientMessage(Component.literal(coloredMessage), true);


       //这里写播放奇异搞笑音效和画面出莫名其妙的字幕
    }

    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, ModifierHooks.INVENTORY_TICK);
    }
}
