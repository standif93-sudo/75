package com.example.forgeeggmod;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod("forgeeggmod")
public class ForgeEggMod {

    public static final String MOD_ID = "forgeeggmod";

    // Регистрируем кастомный тип сущности "Кузница"
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    public static final RegistryObject<EntityType<ForgeEntity>> FORGE_ENTITY = 
            ENTITY_TYPES.register("forge_entity", () -> 
                EntityType.Builder.of(ForgeEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f)
                    .build(new ResourceLocation(MOD_ID, "forge_entity").toString()));

    // Регистрируем яйцо призыва
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<SpawnEggItem> FORGE_SPAWN_EGG = 
            ITEMS.register("forge_spawn_egg", () -> 
                new SpawnEggItem(FORGE_ENTITY.get(), 0x555555, 0xFFAA00, 
                    new Item.Properties().stacksTo(64)));

    public ForgeEggMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreativeTabItems);
        modEventBus.addListener(this::entityAttributes);
        modEventBus.addListener(this::registerSpawnPlacements);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(
            FORGE_ENTITY.get(),
            SpawnPlacements.Type.ON_GROUND,
            Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
            ForgeEntity::checkMobSpawnRules,
            SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Инициализация
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(FORGE_SPAWN_EGG.get());
        }
    }

    private void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(FORGE_ENTITY.get(), ForgeEntity.createAttributes().build());
    }
}
