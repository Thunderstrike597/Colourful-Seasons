package net.kenji.colorful_seasons;

import glitchcore.event.EventManager;
import glitchcore.event.client.RegisterColorsEvent;
import net.kenji.colorful_seasons.api.SeasonalColorConfigValues;
import net.kenji.colorful_seasons.api.SeasonColorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.util.SeasonColorUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockColorHandlers {

    public static void init(){
        EventManager.addListener(BlockColorHandlers::registerBlockColors);
        EventManager.addListener(BlockColorHandlers::registerAdditionalBlockColors);
    }

    static List<TagKey> warmTags = new ArrayList<>();
    static List<TagKey> coldTags = new ArrayList<>();
    static{
        warmTags.add(Tags.Biomes.IS_DRY_OVERWORLD);
        warmTags.add(Tags.Biomes.IS_DRY);
        warmTags.add(Tags.Biomes.IS_SWAMP);
        warmTags.add(Tags.Biomes.IS_DESERT);
        warmTags.add(Tags.Biomes.IS_HOT);

        coldTags.add(Tags.Biomes.IS_COLD);
        coldTags.add(Tags.Biomes.IS_WET);
        coldTags.add(Tags.Biomes.IS_WET_OVERWORLD);
        coldTags.add(Tags.Biomes.IS_SNOWY);
        coldTags.add(Tags.Biomes.IS_MOUNTAIN);
    }

    public static boolean isLeafBlock(BlockState state) {
        // Check the minecraft leaves tag - most modded leaves use this
        if (state.is(BlockTags.LEAVES)) return true;

        // Fallback: check if it has the waterloggable+distance properties
        // that all vanilla-style leaves have
        return state.hasProperty(BlockStateProperties.DISTANCE)
                && state.hasProperty(BlockStateProperties.PERSISTENT);
    }

    private static void registerBlockColors(RegisterColorsEvent.Block event)
    {
        event.register((BlockState state, @Nullable BlockAndTintGetter dimensionReader, @Nullable BlockPos pos, int tintIndex) ->
        {
            int finalColor = FoliageColor.getBirchColor();
            if(!SeasonalColorConfigValues.affectSpruceLeaves) return finalColor;
            Level level = Minecraft.getInstance().player.level();
            ResourceKey<Level> dimension = Minecraft.getInstance().player.level().dimension();

            if (pos != null && ModConfig.seasons.changeBirchColor && ModConfig.seasons.isDimensionWhitelisted(dimension))
            {
                Holder<Biome> biome = level.getBiome(pos);

                if (!biome.is(ModTags.Biomes.BLACKLISTED_BIOMES))
                {
                    ISeasonState calendar = SeasonHelper.getSeasonState(level);
                    ISeasonColorProvider colorProvider = biome.is(ModTags.Biomes.TROPICAL_BIOMES) ? calendar.getTropicalSeason() : calendar.getSubSeason();
                    finalColor = colorProvider.getFoliageOverlay();
                    Season season = SeasonHelper.getSeasonState(level).getSeason();

                    SeasonColorSettings settings = switch (season) {
                        case SPRING -> SeasonalColorConfigValues.FOLIAGE_SPRING;
                        case SUMMER -> SeasonalColorConfigValues.FOLIAGE_SUMMER;
                        case AUTUMN -> SeasonalColorConfigValues.FOLIAGE_AUTUMN;
                        case WINTER -> SeasonalColorConfigValues.FOLIAGE_WINTER;
                    };

                    finalColor =  SeasonalColorOverride.applySettings(finalColor, settings);
                }
            }

            return finalColor;
        }, Blocks.SPRUCE_LEAVES);
    }

    public static boolean isTaggedBiome(List<TagKey> keys, Holder<Biome> biome){
       for(TagKey tag : keys) {
           if(biome.is(tag)){
               return true;
           }
       }
       return false;
    }


    private static void registerAdditionalBlockColors(RegisterColorsEvent.Block event) {

        // Collect all leaf blocks from the registry, excluding vanilla ones
        // you already handle elsewhere
        List<Block> moddedLeaves = ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> {
                    BlockState state = block.defaultBlockState();
                    return isLeafBlock(state)
                            && block != Blocks.OAK_LEAVES
                            && block != Blocks.SPRUCE_LEAVES
                            && block != Blocks.BIRCH_LEAVES
                            && block != Blocks.JUNGLE_LEAVES
                            && block != Blocks.ACACIA_LEAVES
                            && block != Blocks.DARK_OAK_LEAVES
                            && block != Blocks.MANGROVE_LEAVES
                            && block != Blocks.CHERRY_LEAVES
                            && block != Blocks.AZALEA_LEAVES
                            && block != Blocks.FLOWERING_AZALEA_LEAVES;
                })
                .toList();

        event.register((state, dimensionReader, pos, tintIndex) -> {
            Level level = Minecraft.getInstance().player.level();
            if(!SeasonalColorConfigValues.affectModdedBlocks) return FoliageColor.getDefaultColor();
            if (pos == null) return FoliageColor.getDefaultColor();

            Holder<Biome> biome = level.getBiome(pos);

            // Skip tropical biomes - use whatever the block's default is
            if (biome.is(ModTags.Biomes.TROPICAL_BIOMES)) {
                return FoliageColor.getDefaultColor();
            }

            ISeasonState calendar = SeasonHelper.getSeasonState(level);
            ISeasonColorProvider colorProvider = calendar.getSubSeason();
            int finalColor = SeasonColorUtil.applySeasonalFoliageColouring(
                    colorProvider, biome, FoliageColor.getDefaultColor()
            );

            // Apply your color settings
            Season season = calendar.getSeason();
            SeasonColorSettings settings = switch (season) {
                case SPRING -> SeasonalColorConfigValues.FOLIAGE_SPRING;
                case SUMMER -> SeasonalColorConfigValues.FOLIAGE_SUMMER;
                case AUTUMN -> SeasonalColorConfigValues.FOLIAGE_AUTUMN;
                case WINTER -> SeasonalColorConfigValues.FOLIAGE_WINTER;
            };

            if(isTaggedBiome(coldTags, biome) && season != Season.WINTER){
                return FoliageColor.getDefaultColor();
            }



            return SeasonalColorOverride.applySettings(finalColor, settings);

        }, moddedLeaves.toArray(new Block[0]));
    }
}
