package com.example.examplemod;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

//这里的值应该与 META-INF/neoforge.mods.toml 文件中的条目匹配
@Mod(ExampleMod.MODID)
public class ExampleMod
{
    // 在公共位置定义 mod id，以便所有要引用的内容
    public static final String MODID = "examplemod";
    // 直接引用 slf4j 记录器
    private static final Logger LOGGER = LogUtils.getLogger();
    // 创建一个延迟寄存器来保存所有将在“examplemod”命名空间下注册的块
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    //创建一个延迟寄存器来保存所有将在“examplemod”命名空间下注册的项目
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // 创建一个延迟寄存器来保存 CreativeModeTabs，这些 CreativeModeTabs 都将在“examplemod”命名空间下注册
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // 创建一个 id 为“examplemod：example_block”的新块，将命名空间和路径组合在一起
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // 创建一个 id 为“examplemod：example_block”的新 BlockItem，并结合命名空间和路径
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // 创建一个 ID 为“examplemod：example_id”、营养 1 和饱和度 2 的新食品
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // 为示例项目创建一个 ID 为“examplemod：example_tab”的创意标签页，该标签页放置在战斗标签页之后
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //CreativeModeTab 标题的语言键
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // 将示例项添加到选项卡中。对于您自己的选项卡，此方法优先于事件
            }).build());

    // mod 类的构造函数是加载 mod 时运行的第一个代码。
    // FML 将识别一些参数类型，如 IEventBus 或 ModContainer，并自动传入它们。
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // 注册 commonSetup 方法进行 modloading
        modEventBus.addListener(this::commonSetup);

        // 将延迟寄存器注册到 mod 事件总线，以便注册块
        BLOCKS.register(modEventBus);
        //将 Deferred Register 注册到 mod 事件总线，以便注册项目
        ITEMS.register(modEventBus);
        // 将 Deferred Register 注册到 mod 事件总线，以便注册选项卡
        CREATIVE_MODE_TABS.register(modEventBus);

        // 注册我们感兴趣的服务器和其他游戏活动。
        // 请注意，当且仅当我们希望 *this* 类 （ExampleMod） 直接响应事件时，这是必要的。
        // 如果此类中没有@SubscribeEvent注释的函数，请不要添加此行，例如下面的 onServerStarting（）。
        NeoForge.EVENT_BUS.register(this);

        // 将项目注册到广告素材标签页
        modEventBus.addListener(this::addCreative);

        // 注册我们的 mod 的 ModConfigSpec，以便 FML 可以为我们创建和加载配置文件
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // 一些常见的设置代码
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // 将示例块项添加到构建块选项卡
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // 您可以使用 SubscribeEvent 并让事件总线发现要调用的方法
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // 在服务器启动时执行某些操作
        LOGGER.info("HELLO from server starting");
    }

    //可以使用 EventBusSubscriber 自动注册带有 @SubscribeEvent 注释的类中的所有静态方法
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // 一些客户端设置代码
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
