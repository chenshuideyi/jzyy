package com.csdy.until.ReClass;


//public class FuckEventBus extends EventBus {
//    public FuckEventBus(BusBuilderImpl busBuilder) {
//        super(busBuilder);
//    }
//
//    @Override
//    public boolean post(Event event) {
//        return post(event, (IEventListener::invoke));
//    }
//
//    @Override
//    public boolean post(Event event, IEventBusInvokeDispatcher wrapper)
//    {
//
//        if (event instanceof RenderTooltipEvent.Color event1){
//        if (event1.getItemStack().getItem() instanceof BeloveSwordItem) {
//            Random randCol = new Random(BeloveUtils.milliTime());
//            int r = randCol.nextInt();
//            event1.setBackgroundStart(r);
//            event1.setBackgroundEnd(r);
//            event1.setBackground(0);
//            event1.setBorderStart(r);
//            event1.setBorderEnd(r);
//        }
//    } else if (event instanceof RenderTooltipEvent.Pre event1) {
//        if (event1.getItemStack().getItem() instanceof BeloveSwordItem) {
//            event1.setX(8);
//            event1.setY(15);
//            AtomicReference<ResourceLocation> CUSTOM_IMAGE = new AtomicReference<>(new ResourceLocation("belove_sword" , "textures/item/ee.png"));
//            Minecraft mc = Minecraft.getInstance();
//            mc.textureManager.getTexture(CUSTOM_IMAGE.get());
//            Random rand = new Random();
//            int startX = event1.getX()-event1.getX();
//            int startY = event1.getY()-event1.getY();
//            int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
//            int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
//            event1.getGraphics().blit(CUSTOM_IMAGE.get(), startX, startY, 0.0F, 0.0F, width, height, width, height);
//        }
//    }else if (event instanceof ViewportEvent.ComputeFov event1) {
//            if (GodList.isbelovePlayer(event1.getCamera().getEntity())){
//                event1.setFOV(450.0f);
//            }
//        }
//        return event.isCancelable() && event.isCanceled();
//    }



//}
